package com.project.veriphi.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public  UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public User register(User user){
        log.error("");
        try{
            User registered = userRepository.save(user);
            if(registered.getEmail().equals(user.getEmail())){
                log.info("User {} registered successfully", registered.getEmail());
            }
            return registered;
        } catch (Exception e){
            log.error("Cannot register user. Error: {}", e.getMessage());
            return null;
        }
    }

    public User login(String email, String password){
        try{
            System.out.println(userRepository.findByEmailAndPassword(email, password));
        } catch (Exception e){
            log.error("Sign up failed. Error: {}", e.getMessage());
        }
        return null;
    }
}
