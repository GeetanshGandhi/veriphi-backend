package com.project.veriphi.booking;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    List<Booking> findAllByUser(User user);
    List<Booking> findAllByEventScheduleAndSeatCategory(EventSchedule eventSchedule, SeatCategory seatCategory);
}
