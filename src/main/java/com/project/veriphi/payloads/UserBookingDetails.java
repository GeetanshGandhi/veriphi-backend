package com.project.veriphi.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserBookingDetails {
    private String userBookingId;
    private String userEmail;
    private Date bookingDate;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String venue;
    private String seatCategory;
    private String bookingStatus;
    private int numberOfTickets;

    public UserBookingDetails(String userBookingId) {
        this.userBookingId = userBookingId;
    }
}
