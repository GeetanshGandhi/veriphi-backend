package com.project.veriphi.event;

import com.project.veriphi.event_schedule.EventSchedule;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long eventId;

    private String name;
    private String description;
    private String artist;
    private String category;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj==null || this.getClass() != obj.getClass()) return false;
        Event that = (Event) obj;
        return this.eventId == that.eventId;
    }
}
