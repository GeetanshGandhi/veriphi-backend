package com.project.veriphi.organiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/organiser")
@Slf4j
public class OrganiserController {

    @Autowired
    OrganiserService organiserService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/add")
    public ResponseEntity<Organiser> addOrganiser(@RequestParam("organiser") String organiserJson,
                                                  @RequestParam("password") String password){
        try{
            Organiser parsedOrganiserObject = mapper.readValue(organiserJson, Organiser.class);
            Organiser response = organiserService.addOrganiser(parsedOrganiserObject, password);
            if(response == null) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(response.getEmail().equals("unacceptable")) {
                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while JSON parsing. Error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Organiser> login(@RequestParam("email") String email,
                                           @RequestParam("password") String password){
        Organiser response = organiserService.login(email, password);
        if(response == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(response.getEmail().equals("invalid")) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
