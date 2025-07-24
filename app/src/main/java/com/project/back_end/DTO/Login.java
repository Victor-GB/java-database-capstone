package com.project.back_end.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user login credentials.
 */
public class Login {

    private String email;
    private String password;

    public Login() {
        // Default constructor
    }

    /**
     * @return the email address used for login
     */
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password used for login
     */
    @NotBlank(message = "Password cannot be blank")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
