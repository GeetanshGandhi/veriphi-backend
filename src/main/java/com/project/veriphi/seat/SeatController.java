package com.project.veriphi.seat;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.veriphi.seat.payloads.AddSeatPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/seat")
@Slf4j
public class SeatController {

    @Autowired
    SeatService seatService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/addSeats")
    public ResponseEntity<String> addAllSeats(@RequestParam("seatDetails") String seatJson,
                                              @RequestParam("eventId") long eventId,
                                              @RequestParam("venueId") long venueId) {
        try{
            TypeReference<List<AddSeatPayload>> typeReference = new TypeReference<>() {};
            List<AddSeatPayload> inputSeatDetails = mapper.readValue(seatJson, typeReference );
            String response = seatService.addSeats(inputSeatDetails,eventId,venueId);
            if(response.equals("success")) {
                return new ResponseEntity<>("success",HttpStatus.OK);
            }
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            log.error("JSON parsing exception: {}", e.getMessage());
            return new ResponseEntity<>("failure", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    @Autowired
    SeatGenerator seatGenerator;
    @GetMapping("/genseat")
    public void seatgenerate(){
        seatGenerator.generate();
    }
}
