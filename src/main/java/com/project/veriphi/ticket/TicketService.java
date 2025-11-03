package com.project.veriphi.ticket;

import com.project.veriphi.booking.Booking;
import com.project.veriphi.booking.BookingService;
import com.project.veriphi.event_schedule.EventScheduleService;
import com.project.veriphi.payloads.ResoldTicketPayload;
import com.project.veriphi.seat.Seat;
import com.project.veriphi.seat.SeatService;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.seat_category.SeatCategoryService;
import com.project.veriphi.utils.AppConstants;
import com.project.veriphi.utils.EmailService;
import com.project.veriphi.utils.UserBookingIdGenerator;
import com.project.veriphi.utils.external_call.TicketFaceBindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableAsync
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    ResoldTicketRepository rtRepo;
    @Autowired
    BookingService bookingService;
    @Autowired
    SeatService seatService;
    @Autowired
    SeatCategoryService scSvc;
    @Autowired
    EventScheduleService esSvc;
    @Autowired
    TicketFaceBindService tfbService;
    @Autowired
    EmailService emailService;

    @Scheduled(cron = "0 40 16 * * *")
    public void initiateTicketingForBookedBookings() {
        System.out.println("init ticketing");
        List<Booking> bookedBookings = bookingService.getTicketableBookings();
        if(bookedBookings==null || bookedBookings.isEmpty()) return;
        createTicketsForBookings(bookedBookings);
    }

    @Async
    private void createTicketsForBookings(List<Booking> bookings) {
        log.info("Initiating ticketing process for {} bookings.", bookings.size());
        AtomicInteger bookingsProcessed = new AtomicInteger();
        bookings.forEach(booking -> {
            int qty = booking.getNumberOfSeats();
            List<Seat> availableSeats = seatService.getByCategoryAndAllotmentAndLimit(
                            booking.getSeatCategory().getCategoryId(),
                            false,
                            qty
                    );
            List<Ticket> createdTickets = new ArrayList<>();
            List<String> ticketNumbers = new ArrayList<>();
            List<Pair<Seat, Boolean>> allotmentUpdate = new ArrayList<>();
            int seatIndex = 0;
            for(int i = 1; i<=qty; i++) {
                String ticketNumber = booking.getBookingId()+"-"+i;
                Ticket ticket = new Ticket(
                        ticketNumber,
                        booking.getBookingId(),
                        booking.getEventSchedule().getEvent().getEventId(),
                        booking.getEventSchedule().getVenue().getVenueId(),
                        booking.getEventSchedule().getDate(),
                        booking.getEventSchedule().getStartTime(),
                        booking.getBookingEmail(),
                        booking.getSeatCategory().getName(),
                        availableSeats.get(seatIndex).getSeatNumber(),
                        false
                );
                createdTickets.add(ticket);
                ticketNumbers.add(ticketNumber);
                allotmentUpdate.add(Pair.of(availableSeats.get(seatIndex), true));
                seatIndex++;
            }
            //saving changes to db
            seatService.updateSeatAllotment(allotmentUpdate);
            ticketRepository.saveAll(createdTickets);
            bookingService.updateStatus(booking, "allotted");
            //call to bind faces to ticket for user bookings
            if(!booking.isGroup())
              tfbService.callForBinding(ticketNumbers, booking.getBookingId());
            emailService.ticketMail(booking.getBookingEmail(), booking.getBookingEmail(), booking.getBookingId());
            bookingsProcessed.getAndIncrement();
        });
        log.info("Processed {} out of {} bookings successfully", bookingsProcessed.get(), bookings.size());
    }

    public List<Ticket> getAllById(List<String> ticketNumber) {
        try {
            if(ticketNumber.isEmpty()) {
                return new ArrayList<>();
            }
            return ticketRepository.findAllById(ticketNumber);
        } catch (Exception e) {
            log.error("error occurred while getAllById: {}", e.getMessage());
            return null;
        }
    }

    public List<Ticket> getAllByBooking(String bookingId) {
        try {
            return ticketRepository.findAllByBookingId(bookingId);
        } catch (Exception e) {
            log.error("Error while getAllByBooking: {}", e.getMessage());
            return null;
        }
    }

    public String resaleTicket(String ticketNumber) {
        try{
            Optional<Ticket> found = ticketRepository.findById(ticketNumber);
            if(found.isEmpty()) {
                return "no_ticket";
            }
            Ticket ticket = found.get();
            ticket.setResold(true);
            ticketRepository.save(ticket);
            rtRepo.save(
              new ResoldTicket(ticket.getTicketNumber(), AppConstants.TICKET_FOR_SALE)
            );
            return "ok";
        } catch (Exception e) {
            log.error("Error while resaleTicket: {}", e.getMessage());
            return null;
        }
    }

    public List<ResoldTicketPayload> getResaleTicketsForES(long eventId, long venueId, String date, String startTime) {
        try{
            List<Ticket> tickets = ticketRepository.findAllByEventIdAndVenueIdAndDateAndStartTimeAndIsResold(eventId,
                    venueId, date, startTime, true);
            Map<String, SeatCategory> categories = scSvc.getByEventSchedule(esSvc.getById(eventId, venueId, date,
                    startTime)).stream()
                    .collect(Collectors.toMap(SeatCategory::getName, sc -> sc));
            return tickets.stream().map(ticket -> new ResoldTicketPayload(
                    ticket.getTicketNumber(),
                    ticket.getBookingId(),
                    ticket.getEventId(),
                    ticket.getVenueId(),
                    ticket.getDate(),
                    ticket.getStartTime(),
                    ticket.getSeatCategory(),
                    ticket.getSeatNumber(),
                    categories.get(ticket.getSeatCategory()).getPrice()
            )).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error occurred in getResaleTicketsForES: {}", e.getMessage());
            return null;
        }
    }

    public String buyResoldTickets(List<String> ticketNumbers) {
        try{
            List<Ticket> tickets = ticketRepository.findAllById(ticketNumbers);
//            Booking newBooking = new Booking(
//                    UserBookingIdGenerator.createBookingID(),
//                    new Date(),
//            )
            if(tickets.size()!=ticketNumbers.size()) return "not_all";
            for (Ticket ticket : tickets) {
                ticket.setResold(false);
            }
            ticketRepository.saveAll(tickets);
            List<ResoldTicket> resoldTickets = rtRepo.findAllById(ticketNumbers);
            for(ResoldTicket rt: resoldTickets) rt.setStatus(AppConstants.TICKET_RESOLD);
            rtRepo.saveAll(resoldTickets);
            return "ok";
        } catch (Exception e) {
            log.error("Error while buyResoldTicket: {}", e.getMessage());
            return null;
        }
    }

    public String fetchResoldTicketStatus(String ticketNumber){
        try{
            return rtRepo.findById(ticketNumber).get().getStatus();
        } catch (Exception e) {
            log.error("Error while fetchResoldTicketStatus: {}", e.getMessage());
            return null;
        }
    }
}
