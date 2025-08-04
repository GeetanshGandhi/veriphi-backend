package com.project.veriphi.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    UserService userService;
    private static final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestParam("user") String userJson,
                                         @RequestParam("password") String password){
        try {
            User parsedUser = mapper.readValue(userJson, User.class);
            User res = userService.register(parsedUser, password);
            if(res == null) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(res.getEmail().equals("invalid")) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            if(res.getEmail().equals("unknown")) {
                return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
            }
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while Json parsing. Error: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam("email") String email,
                                      @RequestParam("password") String password){
        User user = userService.login(email, password);
        if(user == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(user.getEmail() == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
