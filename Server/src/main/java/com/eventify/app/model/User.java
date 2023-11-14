package com.eventify.app.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.eventify.app.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "user", schema = "eventify")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String firstname;

    @NotNull
    @Column(nullable = false)
    private String lastname;

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    @Email
    @NotNull
    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+)\\.[A-Za-z]{2,4}$")
    private String email;

    @NotNull
    @Size(min = 8, max = 100)
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_picture", referencedColumnName = "id")
    private Photo profilePicture;

    @Enumerated(EnumType.STRING)
    private Role role;

    public String refreshToken;

    public Integer otp;

    @OneToMany(mappedBy = "creator")
    private List<Event> createdEvents;

    @ManyToMany(mappedBy = "participants")
    private List<Event> events;

    public User(String firstname, String lastname, Date dob, String email, String password, Photo profilePicture) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Photo getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(Photo profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Integer getOtp() {
        return this.otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    @Override
    public String toString() {
        return "Eventi{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname=" + lastname +
                ", dob=" + dob +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
