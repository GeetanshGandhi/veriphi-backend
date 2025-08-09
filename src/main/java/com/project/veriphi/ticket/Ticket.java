package com.project.veriphi.ticket;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "veriphiTicket")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @Id
    private String ticketNumber;
    private String bookingId;
    private String userName;
    private long eventId;
    private long venueId;
    private Date date;
    private String startTime;
    private String bookingEmail;
    private String seatNumber;

}
