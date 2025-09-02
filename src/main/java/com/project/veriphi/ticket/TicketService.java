package com.project.veriphi.ticket;

import com.project.veriphi.booking.Booking;
import com.project.veriphi.booking.BookingService;
import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.event_schedule.EventScheduleService;
import com.project.veriphi.seat.Seat;
import com.project.veriphi.seat.SeatService;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.seat_category.SeatCategoryService;
import com.project.veriphi.utils.external_call.TicketFaceBindService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    BookingService bookingService;
    @Autowired
    SeatCategoryService scSvc;
    @Autowired
    SeatService seatService;
    @Autowired
    EventScheduleService esSvc;
    @Autowired
    TicketFaceBindService tfbService;

    @Async
    public void initiateTicketingForEventSchedule(long eventId, long venueId, String date, String startTime) {
        EventSchedule es = esSvc.getById(eventId, venueId, date, startTime);
        createTicketsForEventSchedule(es);
    }

    private void createTicketsForEventSchedule(EventSchedule eventSchedule) {
        log.info("Initiating ticketing process for EventSchedule: {}", eventSchedule.toString());
        List<SeatCategory> categories = scSvc.getByEventAndVenue(eventSchedule.getEvent(), eventSchedule.getVenue());
        log.info("{} categories found for eventSchedule {}", categories.size(), eventSchedule.toString());
        categories.forEach(cat ->{
            List<Seat> seats = seatService.getSeatsByCategory(cat.getCategoryId());
            List<Booking> bookings = bookingService.getAllByEventScheduleAndSeatCategory(eventSchedule, cat);
            log.info("Ticketing process beginning for {} bookings of {} category of {} schedule", bookings.size(),
                    cat.getName(), eventSchedule.toString());
            if(bookings == null || bookings.isEmpty()) {
                return;
            }
            int seatIndex = 0;
            for(Booking booking: bookings) {
                int ticketQty = booking.getNumberOfSeats();
                List<Ticket> tickets = new ArrayList<>();
                List<Seat> allottedSeats = new ArrayList<>();
                for(int i = 1; i<=ticketQty; i++){
                    String ticketNo = booking.getBookingId()+"-"+i;
                    tickets.add(
                            new Ticket(ticketNo,
                                    booking.getBookingId(),
                                    eventSchedule.getEvent().getEventId(),
                                    eventSchedule.getVenue().getVenueId(),
                                    eventSchedule.getDate(),
                                    eventSchedule.getStartTime(),
                                    booking.getUser().getEmail(),
                                    booking.getSeatCategory().getName(),
                                    seats.get(seatIndex).getSeatNumber(),
                                    false
                            )
                    );
                    Seat updatedSeat = seats.get(seatIndex);
                    updatedSeat.setAllotment(true);
                    allottedSeats.add(updatedSeat);
                    seatIndex++;
                }
                ticketRepository.saveAll(tickets);
                seatService.updateSeatAllotment(allottedSeats);

//                boolean ticketBindingOutput = tfbService.callForBinding(tickets, booking.getBookingId());

                log.info("Ticket processing completed for bookingId: {}", booking.getBookingId());
            }
        });
    }
}
