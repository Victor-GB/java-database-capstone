package com.project.back_end.models;

import jakarta.persistence.Entity; // marks this class as a JPA entity
import jakarta.persistence.Id; // primary key identifier
import jakarta.persistence.GeneratedValue; // auto-generate PK
import jakarta.persistence.GenerationType; // generation strategy
import jakarta.validation.constraints.NotNull; // enforce non-null constraints
import jakarta.validation.constraints.Size; // enforce string length constraints
import jakarta.validation.constraints.Email; // validate email format
import jakarta.validation.constraints.Pattern; // validate string against regex
import com.fasterxml.jackson.annotation.JsonProperty; // JSON property control

@Entity // marks this class as a JPA entity
public class Patient {

    @Id // primary key identifier
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-generate PK using IDENTITY strategy
    private Long id;

    @NotNull(message = "name cannot be null") // enforce non-null constraint on name
    @Size(min = 3, max = 100, message = "name must be between 3 and 100 characters") // enforce length constraints on name
    private String name;

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

    @NotNull(message = "address cannot be null") // enforce non-null constraint on address
    @Size(max = 255, message = "address must not exceed 255 characters") // enforce max length on address
    private String address;

    public Patient() {
        // default constructor required by JPA
    }

    public Patient(
            Long id,
            String name,
            String email,
            String password,
            String phone,
            String address
    ) { // parameterized constructor
        this.id = id; // set unique identifier
        this.name = name; // set patient's name
        this.email = email; // set email address
        this.password = password; // set login password
        this.phone = phone; // set phone number
        this.address = address; // set address
    }

    public Long getId() {
        return id; // returns the unique identifier of the patient
    }

    public void setId(Long id) {
        this.id = id; // sets the unique identifier of the patient
    }

    public String getName() {
        return name; // returns the patient's name
    }

    public void setName(String name) {
        this.name = name; // sets the patient's name
    }

    public String getEmail() {
        return email; // returns the patient's email address
    }

    public void setEmail(String email) {
        this.email = email; // sets the patient's email address
    }

    public String getPassword() {
        return password; // returns the patient's password (write-only in JSON)
    }

    public void setPassword(String password) {
        this.password = password; // sets the patient's password
    }

    public String getPhone() {
        return phone; // returns the patient's phone number
    }

    public void setPhone(String phone) {
        this.phone = phone; // sets the patient's phone number
    }

    public String getAddress() {
        return address; // returns the patient's address
    }

    public void setAddress(String address) {
        this.address = address; // sets the patient's address
    }
}
