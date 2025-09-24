package com.project.veriphi.seat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.veriphi.payloads.AddSeatPayload;
import com.project.veriphi.utils.AppConstants;
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
                "Premium", "Gold", "Silver", "Bronze"
        };

        String[] descriptions = {
                "Front row luxury seating",
                "Close to stage with premium comfort",
                "Great view with added benefits",
                "Budget-friendly option with decent view"
        };

        double[] prices = {
                5000.0, 3500.0, 2000.0, 1500.0
        };

        int totalSeats = 2000;
        int baseSeatsPerCategory = totalSeats / categories.length; // 500

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
        // Print total seats generated
        System.out.println("Total seats generated: " + seats.size());

        try {
            String seatJson = AppConstants.OBJECT_MAPPER.writeValueAsString(seats);
            long eventId = 1;
            long venueId = 1;
            String date = "01-10-2025";
            String startTime = "19:00";
            svc.func(seatJson, eventId, venueId, date, startTime);

        } catch (JsonProcessingException e) {
            System.out.println("json error: "+e.getMessage());
        }
    }
}
