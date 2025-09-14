package com.project.veriphi.booking;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    private String bookingId;

    private Date bookingDate;
    private boolean isGroup;
    private int numberOfSeats;

    //can be: booked, allotted, cancelled, event_completed
    private String status;

    @ManyToOne
    @JoinColumn(name = "email")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "eventId", referencedColumnName = "eventId"),
            @JoinColumn(name = "venueId", referencedColumnName = "venueId"),
            @JoinColumn(name = "date", referencedColumnName = "date"),
            @JoinColumn(name = "startTime", referencedColumnName = "startTime")
    })
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private EventSchedule eventSchedule;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private SeatCategory seatCategory;

    public Booking(String bookingId) {
        this.bookingId = bookingId;
    }
}
