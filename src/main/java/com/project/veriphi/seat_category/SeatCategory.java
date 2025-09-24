package com.project.veriphi.seat_category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
