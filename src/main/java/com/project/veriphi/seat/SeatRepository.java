package com.project.veriphi.seat;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends MongoRepository<Seat, String> {

    List<Seat> findAllByCategoryId(String categoryId);
}
