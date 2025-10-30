package com.project.veriphi.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TicketFaceBindPayload {
    private String bookingID;
    private List<String> ticketIDs;
}
