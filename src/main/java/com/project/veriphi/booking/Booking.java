package com.project.veriphi.booking;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.seat_category.SeatCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Booking {

    @Id
    private String bookingId;

    @NonNull private Date bookingDate;
    @NonNull private boolean isGroup;
    @NonNull private String bookingEmail;
    @NonNull private int numberOfSeats;

    //can be: booked, allotted, cancelled, event_completed
    @NonNull private String bookingStatus;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "eventId", referencedColumnName = "eventId"),
            @JoinColumn(name = "venueId", referencedColumnName = "venueId"),
            @JoinColumn(name = "date", referencedColumnName = "date"),
            @JoinColumn(name = "startTime", referencedColumnName = "startTime")
    })
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @NonNull
    private EventSchedule eventSchedule;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @NonNull
    private SeatCategory seatCategory;

}
