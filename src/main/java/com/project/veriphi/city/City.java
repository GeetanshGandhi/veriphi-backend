package com.project.veriphi.city;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {
    @Id
    @Column(columnDefinition = "VARCHAR(6)")
    private String pinCode;
    private String cityName;
    private String state;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj==null || this.getClass() != obj.getClass()) return false;
        City that = (City) obj;
        return this.pinCode.equals(that.pinCode);
    }

    public City(String cityName) {
        this.cityName = cityName;
    }
}
