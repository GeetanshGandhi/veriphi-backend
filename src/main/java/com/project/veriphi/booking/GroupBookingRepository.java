package com.project.veriphi.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupBookingRepository extends JpaRepository<GroupBooking, String> {

    List<GroupBooking> findAllByEmailAndApprovalStatus(String email, String status);
    List<GroupBooking> findAllByApprovalStatusAndBooking_BookingStatus(String approvalStatus, String bookingStatus);
}
