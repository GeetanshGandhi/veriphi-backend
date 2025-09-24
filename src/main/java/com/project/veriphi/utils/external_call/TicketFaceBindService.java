package com.project.veriphi.utils.external_call;

import com.project.veriphi.payloads.BookingTicketPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TicketFaceBindService {

    private static final String BINDING_SERVICE_URL = "http://localhost:8080/seat/addSeats";

    @Autowired
    WebClient webClient;

    public boolean callForBinding(List<String> tickets, String bookingId) {
        BookingTicketPayload btp = new BookingTicketPayload(bookingId, tickets);
        ResponseEntity<String> response = webClient.post()
                .uri(BINDING_SERVICE_URL)
                .bodyValue(btp)
                .retrieve()
                .toEntity(String.class)
                .block();
        return response != null && response.getStatusCode().is2xxSuccessful();
    }

    public void func(String seatJson, long eventId, long venueId, String date, String startTime) {
        System.out.println("calling for seat generation...");
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("seatDetails", seatJson);
        form.add("eventId", String.valueOf(eventId));
        form.add("venueId", String.valueOf(venueId));
        form.add("date", date);
        form.add("startTime",startTime);
        ResponseEntity<String> res =
                webClient.post().uri(BINDING_SERVICE_URL).bodyValue(form).retrieve().toEntity(String.class).block();
        if(res!= null) System.out.println(res.getStatusCode());
        else System.out.println("res is null");
    }
}
