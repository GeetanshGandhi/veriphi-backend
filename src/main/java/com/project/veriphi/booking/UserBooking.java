package com.project.veriphi.booking;

import com.project.veriphi.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class UserBooking {

    @Id
    private String bookingId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "bookingId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NonNull
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "email")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NonNull
    private User user;

    public UserBooking(String bookingId) {
        this.bookingId = bookingId;
    }
}
