package com.project.veriphi.booking;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class GroupBooking {

    @Id
    private String bookingId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "bookingId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NonNull
    private Booking booking;

    @NonNull private String email;
    @NonNull private String entityName;
    @NonNull private String contactNumber;
    @NonNull private String approvalStatus;
}