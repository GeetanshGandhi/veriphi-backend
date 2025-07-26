package com.project.veriphi.city;

import com.project.veriphi.venue.Venue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CityService {

    @Autowired
    CityRepository cityRepository;

    public List<City> findCitiesByState(String state){
        return cityRepository.findAllByState(state);
    }

    public City getCityByPinCode(String pinCode){
        Optional<City> city = cityRepository.findById(pinCode);
        if(city.isPresent()) return city.get();
        log.warn("No city found with pinCode {}. Returning null", pinCode);
        return null;
    }

    public City addCity(City city){
        return cityRepository.save(city);
    }

    public List<City> getAll(){
        return cityRepository.findAll();
    }
}
