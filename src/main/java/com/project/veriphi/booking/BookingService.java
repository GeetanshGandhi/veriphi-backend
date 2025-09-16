package com.project.veriphi.booking;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.user.User;
import com.project.veriphi.user.UserService;
import com.project.veriphi.utils.BookingIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserService userService;

    public Booking createBooking(Booking booking){
        try{
            String bookingId = BookingIdGenerator.createBookingID(booking.getUser().getEmail());
            booking.setBookingId(bookingId);
            Booking saved = bookingRepository.save(booking);
            log.info("booking with ID {} saved successfully", bookingId);
            return saved;
        } catch (Exception e){
            log.error("Could not save booking. Error: {}", e.getMessage());
            return null;
        }
    }

    public void updateStatus(Booking booking, String status) {
        try {
            booking.setBookingStatus(status);
            bookingRepository.save(booking);
        } catch (Exception e) {
            log.error("Error while updating booking status: {}", e.getMessage());
        }
    }

    public List<Booking> getBookingsForUser(String userEmail){
        try{
            User user = userService.getByEmail(userEmail);
            if(user == null) {
                return new ArrayList<>(List.of(new Booking("no-user")));
            }
            return bookingRepository.findAllByUser(user);
        } catch (Exception e){
            log.error("error while getBookingsForUser. Error: {}", e.getMessage());
            return null;
        }
    }

    public Booking getByUserAndES(String userEmail, EventSchedule es) {
        try {
            return bookingRepository.findByUser_EmailAndEventSchedule(userEmail, es);
        } catch (Exception e){
            log.error("Error in getByUserAndES. Error: {}", e.getMessage());
            return null;
        }
    }
    public List<Booking> getAllByEventScheduleAndSeatCategory(EventSchedule eventSchedule, SeatCategory seatCategory) {
        try {
            return bookingRepository.findAllByEventScheduleAndSeatCategory(eventSchedule, seatCategory);
        } catch (Exception e){
            log.error("Error in getAllByEventSchedule. Error: {}", e.getMessage());
            return null;
        }
    }

    public List<Booking> getAllByStatus(String status) {
        try {
            return bookingRepository.findAllByBookingStatus(status);
        } catch (Exception e){
            log.error("Error in getAllByStatus. Error: {}", e.getMessage());
            return null;
        }
    }
}
