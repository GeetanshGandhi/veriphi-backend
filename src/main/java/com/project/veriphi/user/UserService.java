package com.project.veriphi.user;

import com.project.veriphi.utils.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;

    @Autowired
    public  UserService(UserRepository userRepository, UserAuthRepository userAuthRepository){
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
    }

    public User register(User user, String password){
        try{
            if(!EmailValidator.isValidEmail(user.getEmail())){
                return new User("invalid");
            }
            User registered = userRepository.save(user);
            UserAuth registeredAuth = userAuthRepository.save(new UserAuth(user.getEmail(), password));
            if(registered.getEmail().equals(user.getEmail()) &&
               registeredAuth.getEmail().equals(user.getEmail())){
                log.info("User {} registered successfully", registered.getEmail());
                return registered;
            }
            return new User("unknown");
        } catch (Exception e){
            log.error("Cannot register user. Error: {}", e.getMessage());
            return null;
        }
    }

    public User login(String email, String password){
        try{
            UserAuth userAuth = userAuthRepository.findByEmailAndPassword(email, password);
            if(userAuth == null) {
                return new User(null);
            }
            return this.getByEmail(email);
        } catch (Exception e){
            log.error("Error occurred while login. Error: {}", e.getMessage());
            return null;
        }
    }

    public User getByEmail(String email){
        try {
            Optional<User> user = userRepository.findById(email);
            if(user.isPresent()){
                return user.get();
            }
            log.warn("Can't find user with email {}.", email);
            return null;
        } catch (Exception e){
            log.error("Error occurred while getByEmail. Error: {}", e.getMessage());
             return null;
        }
    }
}
