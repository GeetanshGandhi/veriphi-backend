package com.project.veriphi.event;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventService {

    @Autowired
    EventRepository eventRepository;

    public Event addEvent(Event event){
        try{
            event.setApproved(false);
            Event saved = eventRepository.save(event);
            if(saved.getEventId() > 0){
                log.info("Event with id {} saved successfully", event.getEventId());
                return saved;
            }
            log.error("Could not save Event, generated object's eventId: {}", saved.getEventId());
            return null;
        } catch (Exception e){
            log.error("Exception thrown while saving the event. Error: {}", e.getMessage());
            return null;
        }
    }

    public Event updateEvent(Event newDetails){
        Optional<Event> found = eventRepository.findById(newDetails.getEventId());
        if(found.isEmpty()){
            log.error("No event found with eventId {}. Skipping update operation", newDetails.getEventId());
            return null;
        }
        Event savedEvent = found.get();
        savedEvent.setArtist(newDetails.getArtist());
        savedEvent.setCategory(newDetails.getCategory());
        savedEvent.setName(newDetails.getName());
        savedEvent.setDescription(newDetails.getDescription());
        savedEvent.setApproved(true);
        return eventRepository.save(savedEvent);
    }

    public boolean deleteEvent(long eventId){
        try {
            Optional<Event> found = eventRepository.findById(eventId);
            if(found.isEmpty()){
                log.error("No event with id {} found. Skipping delete operation", eventId);
                return false;
            }
            eventRepository.delete(found.get());
            return true;
        } catch (Exception e){
            log.error("Error occurred while deleting event. Error: {}", e.getMessage());
            return false;
        }
    }

    public Event getById(long id){
        Optional<Event> event = eventRepository.findById(id);
        if(event.isEmpty()){
            log.warn("No event with id {} found. Returning null", id);
            return null;
        }
        return event.get();
    }

    public String updateApproval(long eventId, boolean status){
        try {
            Optional<Event> found = eventRepository.findById(eventId);
            if(found.isEmpty()){
                log.warn("No event with id {} found for update.", eventId);
                return "id not found";
            }
            Event event = found.get();
            event.setApproved(status);
            eventRepository.save(event);
            return "success";
        } catch (Exception e){
            log.error("Error occurred while updating event approval. Error: {}", e.getMessage());
            return null;
        }
    }

    public List<Event> getAllEvents(Boolean approved){
        if(approved == null) return eventRepository.findAll();
        return eventRepository.findByApproved(approved);
    }
}
