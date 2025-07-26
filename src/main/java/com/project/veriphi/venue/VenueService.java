package com.project.veriphi.venue;

import com.project.veriphi.city.City;
import com.project.veriphi.city.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VenueService {

    @Autowired
    VenueRepository venueRepository;
    @Autowired
    CityService cityService;

    public Venue addVenue(Venue venue){
        try{
            Venue saved = venueRepository.save(venue);
            if(saved.getVenueId() > 0){
                log.info("Venue information saved successfully!");
                return saved;
            }
            log.error("Could not save venue!");
            return null;
        } catch (Exception e){
            log.error("Exception occurred while saving the venue. Error: {}", e.getMessage());
            return null;
        }
    }

    public boolean deleteVenue(long id){
        Optional<Venue> foundData = venueRepository.findById(id);
        if(foundData.isEmpty()){
            log.error("No venue with given venueId present. Skipping delete operation");
            return false;
        }
        venueRepository.delete(foundData.get());
        log.info("Successfully delete venue with venueId {}", id);
        return true;
    }

    public Venue getById(long id){
        Optional<Venue> venueFound =venueRepository.findById(id);
        if(venueFound.isPresent()) return venueFound.get();
        log.warn("No venue found with id {}. Returning null", id);
        return null;
    }

    public List<Venue> getVenuesByCity(String pinCode){
        City city = cityService.getCityByPinCode(pinCode);
        if(city == null){
            log.warn("No city found with pincode {}. Returning empty list", pinCode);
            return new ArrayList<>();
        }
        return venueRepository.findAllByCity(city);
    }
}
