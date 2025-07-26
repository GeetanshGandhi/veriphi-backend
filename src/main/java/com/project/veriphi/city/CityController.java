package com.project.veriphi.city;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/city")
public class CityController {

    @Autowired
    CityService cityService;

    @PostMapping("/add")
    public City addCity(@RequestBody City city){
        return cityService.addCity(city);
    }

    @GetMapping("/getAll")
    public List<City> getAll(){
        return cityService.getAll();
    }

    @PostMapping("/getByState")
    public List<City> getByState(@RequestBody String state){
        return cityService.findCitiesByState(state);
    }

    @PostMapping("/getByPincode")
    public City getByPincode(@RequestBody String pinCode){
        return cityService.getCityByPinCode(pinCode);
    }
}
