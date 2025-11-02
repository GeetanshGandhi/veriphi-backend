package com.project.veriphi.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GroupBookingDetails {
    @NonNull
    private String groupBookingId;
    private String entityName;
    private String userEmail;
    private Date bookingDate;
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String venue;
    private String seatCategory;
    private String bookingStatus;
    private int numberOfTickets;

}
