package com.eventify.app.model.json;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class CredentialsSignup {
    private String firstname;
    private String lastname;
    private Date dob;
    private String email;
    private String password;
    private MultipartFile profilePicture;
    private String confirmPassword;

    public CredentialsSignup() {

    }

    public CredentialsSignup(String firstname, String lastname, Date dob, String email, String password, MultipartFile profilePicture, String confirmPassword) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        this.confirmPassword = confirmPassword;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getDob() {
        return this.dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MultipartFile getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(MultipartFile profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
