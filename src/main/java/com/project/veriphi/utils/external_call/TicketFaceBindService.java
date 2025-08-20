package com.project.veriphi.utils.external_call;

import com.project.veriphi.ticket.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketFaceBindService {

    private static final String BINDING_SERVICE_URL = "";

    @Autowired
    WebClient webClient;

    public String callForBinding(List<Ticket> tickets, String bookingId) {
        List<String> ticketIds = tickets.stream().map(Ticket::getTicketNumber).toList();

        BookingTicketPayload btp = new BookingTicketPayload(bookingId, ticketIds);
        return webClient.post()
                .uri(BINDING_SERVICE_URL)
                .bodyValue(btp)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
