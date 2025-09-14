package com.project.veriphi.ticket;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    private String ticketNumber;
    private String bookingId;
    private long eventId;
    private long venueId;
    private String date;
    private String startTime;
    private String bookingEmail;
    private String seatCategory;
    private String seatNumber;
    private boolean isResold;

    public Ticket(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
}
