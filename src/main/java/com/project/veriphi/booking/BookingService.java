package com.project.veriphi.booking;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.payloads.GroupBookingCreator;
import com.project.veriphi.payloads.GroupBookingDetails;
import com.project.veriphi.payloads.UserBookingDetails;
import com.project.veriphi.seat_category.SeatCategory;
import com.project.veriphi.seat_category.SeatCategoryService;
import com.project.veriphi.user.User;
import com.project.veriphi.user.UserService;
import com.project.veriphi.utils.LiveBookingCache;
import com.project.veriphi.utils.UserBookingIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {

    private static final String GROUP_APPROVED_STATUS = "Approved";
    private static final String GROUP_REJECTED_STATUS = "Rejected";
    private static final String GROUP_PENDING_STATUS  = "Pending";

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserBookingRepository ubr;
    @Autowired
    GroupBookingRepository gbr;
    @Autowired
    UserService userService;
    @Autowired
    SeatCategoryService scSvc;
    @Autowired
    LiveBookingCache lbc;

    public Booking createBooking(Booking booking){
        try{
            Booking saved = bookingRepository.save(booking);
            log.info("booking with ID {} saved successfully", saved.getBookingId());
            return saved;
        } catch (Exception e){
            log.error("Could not save booking. Error: {}", e.getMessage());
            return null;
        }
    }

    public UserBooking createUserBooking(UserBooking userBooking) {
        try{
            UserBooking saved = ubr.save(userBooking);
            log.info("UserBooking with ID {} saved successfully", saved.getBookingId());
            return saved;
        } catch (Exception e){
            log.error("Could not save userBooking. Error: {}", e.getMessage());
            return null;
        }
    }

    public void updateStatus(Booking booking, String status) {
        try {
            booking.setBookingStatus(status);
            bookingRepository.save(booking);
        } catch (Exception e) {
            log.error("Error while updating booking status: {}", e.getMessage());
        }
    }

    public List<UserBookingDetails> getBookingsForUser(String userEmail){
        try{
            User user = userService.getByEmail(userEmail);
            if(user == null) {
                return List.of(new UserBookingDetails("-1"));
            }
            List<UserBooking> found = ubr.findAllByUser(user);
            List<UserBookingDetails> output = new ArrayList<>();
            for(UserBooking ub: found) {
                output.add(new UserBookingDetails(
                        ub.getBookingId(),
                        ub.getUser().getEmail(),
                        ub.getBooking().getBookingDate(),
                        ub.getBooking().getEventSchedule().getEvent().getName(),
                        ub.getBooking().getEventSchedule().getDate(),
                        ub.getBooking().getEventSchedule().getStartTime(),
                        ub.getBooking().getEventSchedule().getVenue().getName(),
                        ub.getBooking().getSeatCategory().getName(),
                        ub.getBooking().getBookingStatus(),
                        ub.getBooking().getNumberOfSeats()
                ));
            }
            return output;
        } catch (Exception e){
            log.error("error while getBookingsForUser. Error: {}", e.getMessage());
            return null;
        }
    }

    public Booking getById(String bookingId) {
        try {
            Optional<Booking> booking = bookingRepository.findById(bookingId);
            return booking.orElse(null);
        } catch (Exception e){
            log.error("Error in getById. Error: {}", e.getMessage());
            return null;
        }
    }
    public UserBooking getByUserAndES(String userEmail, EventSchedule es) {
        try {
            return ubr.findByUser_EmailAndBooking_EventSchedule(userEmail, es);
        } catch (Exception e){
            log.error("Error in getByUserAndES. Error: {}", e.getMessage());
            return null;
        }
    }

    public List<Booking> getTicketableBookings() {
        try {
            List<Booking> ticketableBookings = bookingRepository.findAllByBookingStatusAndIsGroup("booked", false);
            ticketableBookings.addAll(
                    gbr.findAllByApprovalStatusAndBooking_BookingStatus(GROUP_APPROVED_STATUS, "booked")
                            .stream()
                            .map(GroupBooking::getBooking)
                            .collect(Collectors.toSet())
            );
            return ticketableBookings;
        } catch (Exception e){
            log.error("Error in getTicketableBookings. Error: {}", e.getMessage());
            return null;
        }
    }

    public String createGroupBooking(GroupBookingCreator gbc, EventSchedule es, SeatCategory sc) {
        try {
            String groupBookingId = UserBookingIdGenerator.createBookingID(gbc.getEmail());
            Booking booking = new Booking(
                    groupBookingId,
                    new Date(),
                    true,
                    gbc.getEmail(),
                    gbc.getNumberOfTickets(),
                    "booked",
                    es,
                    sc
            );
            Booking savedBooking = bookingRepository.save(booking);
            GroupBooking groupBooking = new GroupBooking(
                    savedBooking,
                    gbc.getEntityName(),
                    gbc.getEmail(),
                    gbc.getContactNumber(),
                    "Pending"
            );
            GroupBooking sgb = gbr.save(groupBooking);
            if(sgb.getBookingId().equals(groupBookingId))
                return sgb.getBookingId();
            return null;
        } catch (Exception e) {
            log.error("Error occurred while createGroupBooking: {}", e.getMessage());
            return null;
        }
    }

    public String updateGroupApproval(String bookingId, String approval) {
        if(approval.equals(GROUP_APPROVED_STATUS) || approval.equals(GROUP_REJECTED_STATUS)) {
            Optional<GroupBooking> groupBooking = gbr.findById(bookingId);
            if(groupBooking.isEmpty()) return "not_found";
            GroupBooking found = groupBooking.get();
            found.setApprovalStatus(approval);
            if(approval.equals(GROUP_APPROVED_STATUS)) {
                found.setApprovalStatus(GROUP_APPROVED_STATUS);
                GroupBooking updated = gbr.save(found);
                SeatCategory categoryToUpdate = updated.getBooking().getSeatCategory();
                categoryToUpdate.setCurrentAvailability(
                        categoryToUpdate.getCurrentAvailability() - updated.getBooking().getNumberOfSeats()
                );
                scSvc.updateSeatCategory(categoryToUpdate);
                if(found.getBooking().getEventSchedule().isSaleLive())
                    lbc.updateSeatCountForEventSchedule(
                        found.getBooking().getEventSchedule(),
                        categoryToUpdate.getCategoryId(),
                        found.getBooking().getNumberOfSeats(),
                        5
                );
                return GROUP_APPROVED_STATUS;
            }
            else {
                Booking booking = found.getBooking();
                this.updateStatus(booking, "Cancelled");
                found.setApprovalStatus(GROUP_REJECTED_STATUS);
                gbr.save(found);
                return GROUP_REJECTED_STATUS;
            }
        }
        return "unprocessable";
    }

    public List<GroupBookingDetails> getApprovedGroupBookingsForEmail(String email) {
        try {
            List<GroupBooking> bookings = gbr.findAllByEmailAndApprovalStatus(email, GROUP_APPROVED_STATUS);
            List<GroupBookingDetails> output = new ArrayList<>();
            for (GroupBooking sgb: bookings) {
                output.add(new GroupBookingDetails(
                        sgb.getBooking().getBookingId(),
                        sgb.getEmail(),
                        sgb.getBooking().getBookingDate(),
                        sgb.getBooking().getEventSchedule().getEvent().getName(),
                        sgb.getBooking().getEventSchedule().getDate(),
                        sgb.getBooking().getEventSchedule().getStartTime(),
                        sgb.getBooking().getEventSchedule().getVenue().getName(),
                        sgb.getBooking().getSeatCategory().getName(),
                        sgb.getBooking().getBookingStatus(),
                        sgb.getBooking().getNumberOfSeats()
                ));
            }
            return output;
        } catch (Exception e) {
            log.error("Error while getApprovedGroupBookingsForEmail: {}", e.getMessage());
            return null;
        }
    }

    public List<GroupBookingDetails> getPendingOrRejectedGroupBookingsForEmail(String email) {
        try {
            List<GroupBooking> bookings = gbr.findAllByEmailAndApprovalStatus(email, GROUP_REJECTED_STATUS);
            bookings.addAll(gbr.findAllByEmailAndApprovalStatus(email, GROUP_PENDING_STATUS));
            List<GroupBookingDetails> output = new ArrayList<>();
            for (GroupBooking sgb: bookings) {
                output.add(new GroupBookingDetails(
                        sgb.getBooking().getBookingId(),
                        sgb.getEmail(),
                        sgb.getBooking().getBookingDate(),
                        sgb.getBooking().getEventSchedule().getEvent().getName(),
                        sgb.getBooking().getEventSchedule().getDate(),
                        sgb.getBooking().getEventSchedule().getStartTime(),
                        sgb.getBooking().getEventSchedule().getVenue().getName(),
                        sgb.getBooking().getSeatCategory().getName(),
                        sgb.getBooking().getBookingStatus(),
                        sgb.getBooking().getNumberOfSeats()
                ));
            }
            return output;
        } catch (Exception e) {
            log.error("Error while getPendingOrRejectedGroupBookingsForEmail: {}", e.getMessage());
            return null;
        }
    }
}


