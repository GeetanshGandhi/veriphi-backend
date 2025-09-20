package com.project.veriphi.seat_category;

import com.project.veriphi.event.Event;
import com.project.veriphi.venue.Venue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "venueId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Venue venue;

    @ManyToOne
    @JoinColumn(name = "eventId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;
}
