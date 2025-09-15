package com.project.veriphi.utils.external_call;

import com.project.veriphi.ticket.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketFaceBindService {

    private static final String BINDING_SERVICE_URL = "http://localhost:8080/seat/addSeats";

    @Autowired
    WebClient webClient;

    public boolean callForBinding(List<Ticket> tickets, String bookingId) {
        List<String> ticketIds = tickets.stream().map(Ticket::getTicketNumber).toList();

        BookingTicketPayload btp = new BookingTicketPayload(bookingId, ticketIds);
        ResponseEntity<String> response = webClient.post()
                .uri(BINDING_SERVICE_URL)
                .bodyValue(btp)
                .retrieve()
                .toEntity(String.class)
                .block();
        return response.getStatusCode().is2xxSuccessful();
    }

    public void func(String seatJson, long eventId, long venueId) {
        System.out.println("calling for seat generation...");
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("seatDetails", seatJson);
        form.add("eventId", String.valueOf(eventId));
        form.add("venueId", String.valueOf(venueId));
        ResponseEntity<String> res =
                webClient.post().uri(BINDING_SERVICE_URL).bodyValue(form).retrieve().toEntity(String.class).block();
        if(res!= null) System.out.println(res.getStatusCode().toString());
        else System.out.println("res is null");
    }
}
