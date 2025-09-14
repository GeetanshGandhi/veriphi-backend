package com.project.veriphi.ticket;

import com.project.veriphi.booking.BookingService;
import com.project.veriphi.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketResaleService {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;

}
