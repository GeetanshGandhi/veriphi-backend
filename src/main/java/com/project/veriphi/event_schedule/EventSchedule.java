package com.project.veriphi.event_schedule;

import com.project.veriphi.event.Event;
import com.project.veriphi.venue.Venue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@IdClass(EventScheduleId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventSchedule {

    @Id
    private Date date;
    @Id
    private String startTime;

    @Id
    @ManyToOne
    @JoinColumn(name = "eventId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @Id
    @ManyToOne
    @JoinColumn(name = "venueId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Venue venue;

    private double durationHours;
    private int maxAttendees;
    private Date scheduledSaleStart;
    private boolean saleLive;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj==null || this.getClass() != obj.getClass()) return false;
        EventSchedule that = (EventSchedule) obj;
        return Objects.equals(this.event, that.event) &&
                Objects.equals(this.venue, that.venue) &&
                Objects.equals(this.date, that.date) &&
                Objects.equals(this.startTime, that.startTime) &&
                Objects.equals(this.scheduledSaleStart, that.scheduledSaleStart) &&
                this.durationHours==that.durationHours &&
                this.saleLive == that.isSaleLive() &&
                this.maxAttendees == that.maxAttendees;
    }
}
