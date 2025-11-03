package com.project.veriphi.utils.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/email")
public class EmailController {

    @Autowired
    EmailService emailService;

    @GetMapping("/send")
    public void func(){
        System.out.println("email endpoint hit");
        emailService.random("Coldplay Concert", "viono", "fuvb", "geetanshgandhi2509@gmail.com", "Geetansh",
                "32524grbf49btv5");
    }
}
