package com.project.veriphi.event;

import com.project.veriphi.event_schedule.EventSchedule;
import com.project.veriphi.organiser.Organiser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Entity
@Data
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
    private Boolean approved;

    @ManyToOne
    @JoinColumn(name = "email")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Organiser organiser;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj==null || this.getClass() != obj.getClass()) return false;
        Event that = (Event) obj;
        return this.eventId == that.eventId;
    }
}
