package com.project.veriphi.seat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.veriphi.seat.payloads.AddSeatPayload;
import com.project.veriphi.utils.external_call.TicketFaceBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SeatGenerator {

    @Autowired
    TicketFaceBindService svc;
    public void generate() {
        List<AddSeatPayload> seats = new ArrayList<>();

        String[] categories = {
                "VIP", "Premium", "Gold", "Silver", "Bronze", "Economy"
        };

        String[] descriptions = {
                "Front row luxury seating",
                "Close to stage with premium comfort",
                "Great view with added benefits",
                "Mid-tier affordable seating",
                "Budget-friendly option with decent view",
                "Basic seating for general audience"
        };

        double[] prices = {
                5000.0, 3500.0, 2500.0, 1500.0, 800.0, 400.0
        };

        int totalSeats = 4800;
        int baseSeatsPerCategory = totalSeats / categories.length; // 800

        int seatCounter = 1;
        for (int i = 0; i < categories.length; i++) {
            for (int j = 0; j < baseSeatsPerCategory; j++) {
                String seatNumber = "S" + String.format("%04d", seatCounter++);
                seats.add(new AddSeatPayload(
                        seatNumber,
                        categories[i],
                        descriptions[i],
                        prices[i]
                ));
            }
        }

        // Print first 10 as sample
        for (int i = 0; i < 10; i++) {
            System.out.println(seats.get(i));
        }

        // Print total seats generated
        System.out.println("Total seats generated: " + seats.size());

        try {
            String seatJson = new ObjectMapper().writeValueAsString(seats);
            long eventId = 2;
            long venueId = 3;
            svc.func(seatJson, eventId, venueId);

        } catch (JsonProcessingException e) {
            System.out.println("json error: "+e.getMessage());
        }
    }
}
