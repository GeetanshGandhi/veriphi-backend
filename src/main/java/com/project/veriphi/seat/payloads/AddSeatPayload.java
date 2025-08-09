package com.project.veriphi.seat.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddSeatPayload {
    private String seatNumber;
    private String categoryName;
    private String categoryDescription;
    private double price;
}
