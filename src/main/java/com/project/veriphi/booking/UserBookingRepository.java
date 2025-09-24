package com.project.veriphi.booking;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBookingRepository extends JpaRepository<UserBooking, String> {

    List<UserBooking> findAllByUser(User user);
    UserBooking findByUser_EmailAndBooking_EventSchedule(String userEmail, EventSchedule eventSchedule);
}
