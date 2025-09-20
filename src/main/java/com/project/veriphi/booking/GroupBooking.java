package com.project.veriphi.booking;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupBooking {

    @Id
    private String bookingId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "bookingId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Booking booking;

    private String entityName;
    private String email;
    private String contactNumber;
}