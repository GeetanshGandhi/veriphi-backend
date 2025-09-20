package com.project.veriphi.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupBookingCreator {

    private long eventId;
    private long venueId;
    private String date;
    private String startTime;
    private String entityName;
    private String email;
    private String contactNumber;
    private String seatCategoryId;
    private int numberOfTickets;
}
