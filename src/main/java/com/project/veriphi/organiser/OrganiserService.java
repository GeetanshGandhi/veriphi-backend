package com.project.veriphi.organiser;

import com.project.veriphi.utils.EmailValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class OrganiserService {

    @Autowired
    OrganiserRepository organiserRepository;
    @Autowired
    OrganiserAuthRepository organiserAuthRepository;

    public Organiser addOrganiser(Organiser organiser, String password) {
        try{
            if(!EmailValidator.isValidEmail(organiser.getEmail())){
                return new Organiser("unacceptable");
            }
            Organiser registered = organiserRepository.save(organiser);

            OrganiserAuth authToSave = new OrganiserAuth(organiser.getEmail(), password);
            OrganiserAuth registeredAuth = organiserAuthRepository.save(authToSave);

            if(registered.getEmail().equals(organiser.getEmail()) &&
               registeredAuth.getEmail().equals(organiser.getEmail())){
                log.info("Organiser {} registered successfully", registered.getEmail());
            }
            return registered;
        } catch (Exception e){
            log.error("Cannot register organiser. Error: {}", e.getMessage());
            return null;
        }
    }

    public Organiser login(String email, String password) {
        try{
            OrganiserAuth organiserAuth = organiserAuthRepository.findByEmailAndPassword(email, password);
            if(organiserAuth == null) {
                new Organiser("invalid");
            }
            return organiserRepository.findById(organiserAuth.getEmail()).get();
        } catch (Exception e){
            log.error("Error occurred while login. Error: {}", e.getMessage());
            return null;
        }
    }

    public Organiser getById(String email) {
        Optional<Organiser> found = organiserRepository.findById(email);
        if(found.isEmpty()) {
            log.warn("No organiser with email {} exists.", email);
            return null;
        }
        return found.get();
    }
}
