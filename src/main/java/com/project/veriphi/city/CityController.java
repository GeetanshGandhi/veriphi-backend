package com.project.veriphi.city;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

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

    @GetMapping("/getByState")
    public List<City> getByState(@RequestBody String state){
        return cityService.findCitiesByState(state);
    }

    @GetMapping("/getByPincode")
    public City getByPinCode(@RequestBody String pinCode){
        return cityService.getCityByPinCode(pinCode);
    }
}
