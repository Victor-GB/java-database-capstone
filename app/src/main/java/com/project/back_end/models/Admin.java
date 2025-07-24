package com.project.back_end.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity // marks this class as a JPA entity
public class Admin {

    @Id // primary key identifier
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-generate PK using IDENTITY strategy
    private Long id;

    @NotNull // enforce non-null constraint on username
    private String username;

    @NotNull // enforce non-null constraint on password
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // hide password field in JSON responses
    private String password;

    public Admin() { } // default constructor required by JPA

    public Admin(Long id, String username, String password) { // parameterized constructor
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
