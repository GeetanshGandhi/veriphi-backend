package com.project.veriphi.ticket;

import com.project.veriphi.booking.BookingService;
import com.project.veriphi.user.User;
import com.project.veriphi.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketResaleService {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;

//    public String initiateResaleForUser(String email, String bookingId, List<String> ticketNumbers) {
//        User user = userService.getByEmail(email);
//        if(user == nulxl ){
//            return "usernotfound";
//        }
//
//    }
}
