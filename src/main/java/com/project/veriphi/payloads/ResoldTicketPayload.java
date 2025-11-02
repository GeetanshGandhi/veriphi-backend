package com.project.veriphi.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResoldTicketPayload {
    private String ticketNumber;
    private String bookingId;
    private long eventId;
    private long venueId;
    private String date;
    private String startTime;
    private String seatCategory;
    private String seatNumber;
    private double price;
}
