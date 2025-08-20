package com.project.veriphi.utils.external_call;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingTicketPayload {
    private String bookingID;
    private List<String> ticketIDs;
}
