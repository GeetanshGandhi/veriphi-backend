package com.project.veriphi.organiser;

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
public class Organiser {

    @Id
    private String email;
    private String name;
    private String contactPerson;
    @Column(columnDefinition = "VARCHAR(10)")
    private String contactNumber;

    public Organiser(String email) {
        this.email = email;
    }
}
