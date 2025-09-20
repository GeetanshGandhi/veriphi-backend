package com.project.veriphi.seat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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
    private boolean allotment;
}
