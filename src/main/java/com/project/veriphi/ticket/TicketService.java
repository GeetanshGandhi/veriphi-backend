package com.project.veriphi.ticket;

import com.project.veriphi.booking.Booking;
import com.project.veriphi.booking.BookingService;
import com.project.veriphi.seat.Seat;
import com.project.veriphi.seat.SeatService;
import com.project.veriphi.utils.external_call.TicketFaceBindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@EnableAsync
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    BookingService bookingService;
    @Autowired
    SeatService seatService;
    @Autowired
    TicketFaceBindService tfbService;

    @Scheduled(cron = "0 0 16 * * *")
    public void initiateTicketingForBookedBookings() {
        List<Booking> bookedBookings = bookingService.getTicketableBookings();
        if(bookedBookings==null || bookedBookings.isEmpty()) return;
        createTicketsForBookings(bookedBookings);
    }

    @Async
    private void createTicketsForBookings(List<Booking> bookings) {
        log.info("Initiating ticketing process {} bookings.", bookings.size());
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
//            if(!booking.isGroup())
//              tfbService.callForBinding(ticketNumbers, userBooking.getBookingId());
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

}
