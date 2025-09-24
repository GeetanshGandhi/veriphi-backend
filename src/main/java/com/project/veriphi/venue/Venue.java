package com.project.veriphi.venue;

import com.project.veriphi.city.City;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long venueId;

    private String name;
    private String address;

    @Column(columnDefinition = "VARCHAR(10)")
    private String contact;
    private String email;

    @ManyToOne
    @JoinColumn(name = "pinCode")
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    private City city;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj==null || this.getClass() != obj.getClass()) return false;
        Venue that = (Venue) obj;
        return this.venueId == that.venueId;
    }

}
