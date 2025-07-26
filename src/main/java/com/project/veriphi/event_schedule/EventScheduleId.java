package com.project.veriphi.event_schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventScheduleId implements Serializable {

    private long event;
    private long venue;
    private Date date;
    private String startTime;

    @Override
    public int hashCode() {
        return Objects.hash(event, venue, date, startTime);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj==null || this.getClass() != obj.getClass()) return false;
        EventScheduleId that = (EventScheduleId) obj;
        return Objects.equals(this.event, that.event) && Objects.equals(this.venue, that.venue) && Objects.equals(this.date, that.date) && Objects.equals(this.startTime, that.startTime);
    }
}
