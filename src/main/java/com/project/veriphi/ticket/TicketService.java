package com.project.veriphi.ticket;

import com.project.veriphi.booking.Booking;
import com.project.veriphi.booking.BookingService;
import com.project.veriphi.booking.UserBooking;
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

    @Async
    @Scheduled(cron = "0 0 16 * * *")
    public void initiateTicketingForEventSchedule() {
        List<UserBooking> bookedBookings = bookingService.getUserBookingsByStatus("booked");
        if(bookedBookings==null || bookedBookings.isEmpty()) return;
        createTicketsForUserBookings(bookedBookings);
    }

    private void createTicketsForUserBookings(List<UserBooking> bookings) {
        log.info("Initiating ticketing process {} bookings.", bookings.size());
        AtomicInteger bookingsProcessed = new AtomicInteger();
        bookings.forEach(userBooking -> {
            int qty = userBooking.getBooking().getNumberOfSeats();
            List<Seat> availableSeats = seatService.getByCategoryAndAllotmentAndLimit(
                            userBooking.getBooking().getSeatCategory().getCategoryId(),
                            false,
                            qty
                    );
            List<Ticket> createdTickets = new ArrayList<>();
            List<String> ticketNumbers = new ArrayList<>();
            List<Pair<Seat, Boolean>> allotmentUpdate = new ArrayList<>();
            int seatIndex = 0;
            for(int i = 1; i<=qty; i++) {
                String ticketNumber = userBooking.getUserBookingId()+"-"+i;
                Ticket ticket = new Ticket(
                        ticketNumber,
                        userBooking.getUserBookingId(),
                        userBooking.getBooking().getEventSchedule().getEvent().getEventId(),
                        userBooking.getBooking().getEventSchedule().getVenue().getVenueId(),
                        userBooking.getBooking().getEventSchedule().getDate(),
                        userBooking.getBooking().getEventSchedule().getStartTime(),
                        userBooking.getUser().getEmail(),
                        userBooking.getBooking().getSeatCategory().getName(),
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
            bookingService.updateStatus(userBooking.getBooking(), "allotted");
            //call to bind faces to ticket
//            tfbService.callForBinding(ticketNumbers, userBooking.getBookingId());
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
