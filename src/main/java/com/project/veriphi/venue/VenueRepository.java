package com.project.veriphi.venue;

import com.project.veriphi.city.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findAllByCity(City city);
}
