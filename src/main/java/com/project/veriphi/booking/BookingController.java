package com.project.veriphi.booking;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.event_schedule.EventScheduleService;
import com.project.veriphi.payloads.GroupBookingCreator;
import com.project.veriphi.payloads.GroupBookingDetails;
import com.project.veriphi.payloads.UserBookingDetails;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.seat_category.SeatCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/booking")
@Slf4j
public class BookingController {

    @Autowired
    BookingService bookingService;
    @Autowired
    EventScheduleService esSvc;
    @Autowired
    SeatCategoryService scSvc;

    @PostMapping("/getForUser")
    public ResponseEntity<List<UserBookingDetails>> getBookingsForUser(@RequestBody String email) {
        try {
            List<UserBookingDetails> bookings = bookingService.getBookingsForUser(email);
            if(bookings == null) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(!bookings.isEmpty() && bookings.getFirst().getUserBookingId().equalsIgnoreCase("-1")) {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNPROCESSABLE_ENTITY);
            }
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while retrieving bookings for user: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createGroup")
    public ResponseEntity<String> createGroupBooking(@RequestBody GroupBookingCreator gbc) {
        try{
            EventSchedule esFound = esSvc.getById(gbc.getEventId(), gbc.getVenueId(), gbc.getDate(),
                    gbc.getStartTime());
            SeatCategory scFound = scSvc.getById(gbc.getSeatCategoryId());
            if(esFound == null || scFound == null) {
                log.warn("Cannot find eventSchedule or seatCategory for input: {}", gbc);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            String output = bookingService.createGroupBooking(gbc, esFound, scFound);
            return new ResponseEntity<>(
                    output,
                    output==null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Error at endpoint createGroupBooking: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateGroupApproval")
    public ResponseEntity<String> updateApprovalForGroup(@RequestParam("bookingId") String bookingId,
                                                         @RequestParam("approval") String approval) {
        try{
            String output = bookingService.updateGroupApproval(bookingId, approval);
            if(output.equals("unprocessable")) {
                return new ResponseEntity<>(null, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            return new ResponseEntity<>(output, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error at updateGroupApproval endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getGrpApprovedByEmail")
    public ResponseEntity<List<GroupBookingDetails>> getGroupAppEmail(@RequestBody String email) {
        try{
            List<GroupBookingDetails> output = bookingService.getApprovedGroupBookingsForEmail(email);
            if(output == null) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(output, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error at getGrpApprovedByEmail endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getGrpPendingOrRejectByEmail")
    public ResponseEntity<List<GroupBookingDetails>> getGroupPenRejEmail(@RequestBody String email) {
        try{
            List<GroupBookingDetails> output = bookingService.getPendingOrRejectedGroupBookingsForEmail(email);
            if(output == null) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(output, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error at getGrpPendingOrRejectByEmail endpoint: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllGroupPending")
    public ResponseEntity<List<GroupBookingDetails>> getAllPending(){
        try{
            List<GroupBookingDetails> output = bookingService.getAllPending();
            if(output == null) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(output, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error at endpoint getAllPending: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
