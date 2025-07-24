package com.project.back_end.models;

import jakarta.persistence.Entity; // marks this class as a JPA entity
import jakarta.persistence.Id; // primary key identifier
import jakarta.persistence.GeneratedValue; // auto-generate PK
import jakarta.persistence.GenerationType; // generation strategy
import jakarta.persistence.ElementCollection; // for basic collections
import jakarta.validation.constraints.NotNull; // enforce non-null constraints
import jakarta.validation.constraints.Size; // enforce string length constraints
import jakarta.validation.constraints.Email; // validate email format
import jakarta.validation.constraints.Pattern; // validate string against regex
import com.fasterxml.jackson.annotation.JsonProperty; // JSON property control
import java.util.List; // for collection of available times

@Entity // marks this class as a JPA entity
public class Doctor {

    @Id // primary key identifier
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-generate PK using IDENTITY strategy
    private Long id;

    @NotNull(message = "name cannot be null") // enforce non-null constraint on name
    @Size(min = 3, max = 100, message = "name must be between 3 and 100 characters") // enforce length constraints on name
    private String name;

    @NotNull(message = "specialty cannot be null") // enforce non-null constraint on specialty
    @Size(min = 3, max = 50, message = "specialty must be between 3 and 50 characters") // enforce length constraints on specialty
    private String specialty;

    @NotNull(message = "email cannot be null") // enforce non-null constraint on email
    @Email(message = "email must be a valid format") // validate email format
    private String email;

    @NotNull(message = "password cannot be null") // enforce non-null constraint on password
    @Size(min = 6, message = "password must be at least 6 characters") // enforce minimum length on password
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // hide password field in JSON responses
    private String password;

    @NotNull(message = "phone cannot be null") // enforce non-null constraint on phone
    @Pattern(regexp = "^[0-9]{10}$", message = "phone must be exactly 10 digits") // validate phone format
    private String phone;

    @ElementCollection // stores list of time slots in a separate collection table
    private List<String> availableTimes;

    public Doctor() {
        // default constructor required by JPA
    }

    public Doctor(
            Long id,
            String name,
            String specialty,
            String email,
            String password,
            String phone,
            List<String> availableTimes
    ) { // parameterized constructor
        this.id = id; // set unique identifier
        this.name = name; // set doctor's name
        this.specialty = specialty; // set medical specialty
        this.email = email; // set email address
        this.password = password; // set login password
        this.phone = phone; // set phone number
        this.availableTimes = availableTimes; // set list of available time slots
    }

    public Long getId() {
        return id; // returns the unique identifier of the doctor
    }

    public void setId(Long id) {
        this.id = id; // sets the unique identifier of the doctor
    }

    public String getName() {
        return name; // returns the doctor's name
    }

    public void setName(String name) {
        this.name = name; // sets the doctor's name
    }

    public String getSpecialty() {
        return specialty; // returns the doctor's specialty
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty; // sets the doctor's specialty
    }

    public String getEmail() {
        return email; // returns the doctor's email address
    }

    public void setEmail(String email) {
        this.email = email; // sets the doctor's email address
    }

    public String getPassword() {
        return password; // returns the doctor's password (write-only in JSON)
    }

    public void setPassword(String password) {
        this.password = password; // sets the doctor's password
    }

    public String getPhone() {
        return phone; // returns the doctor's phone number
    }

    public void setPhone(String phone) {
        this.phone = phone; // sets the doctor's phone number
    }

    public List<String> getAvailableTimes() {
        return availableTimes; // returns the list of available time slots
    }

    public void setAvailableTimes(List<String> availableTimes) {
        this.availableTimes = availableTimes; // sets the list of available time slots
    }
}
