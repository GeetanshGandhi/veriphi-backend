package com.project.veriphi.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Email(message = "Invalid email!")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    private String mobile;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingPincode;

    public User(String email) {
        this.email = email;
    }
}
