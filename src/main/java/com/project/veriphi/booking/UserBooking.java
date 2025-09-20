package com.project.veriphi.booking;

import com.project.veriphi.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBooking {

    @Id
    private String userBookingId;

    @ManyToOne
    @JoinColumn(name = "bookingId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "email")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public UserBooking(String userBookingId) {
        this.userBookingId = userBookingId;
    }
}
