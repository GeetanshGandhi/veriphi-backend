package com.project.veriphi.seat_category;

import com.project.veriphi.event.Event;
import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.venue.Venue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatCategory {

    @Id
    private String categoryId;
    private String name;
    private String description;
    private double price;
    private int maxAvailability;
    private int currentAvailability;
    private long eventId;
    private long venueId;
    private String date;
    private String startTime;
}
