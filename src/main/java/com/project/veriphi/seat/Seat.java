package com.project.veriphi.seat;

import com.project.veriphi.seat_category.SeatCategory;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seat {

    @Id
    private String seatId;
    private String seatNumber;

    private String categoryId;

    @Transient
    private SeatCategory seatCategory;
}
