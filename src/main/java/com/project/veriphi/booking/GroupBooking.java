package com.project.veriphi.booking;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupBooking {

    @Id
    private String groupBookingId;

    @ManyToOne
    @JoinColumn(name = "bookingId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Booking booking;

    private String entityName;
    private String email;
    private String contactNumber;

}