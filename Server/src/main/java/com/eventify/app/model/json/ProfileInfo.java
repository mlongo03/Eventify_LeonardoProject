package com.eventify.app.model.json;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileInfo {

    private MultipartFile photos;

    @NotNull(message = "First name is mandatory")
    @NotBlank(message = "First name is mandatory")
    private String firstname;
    @NotNull(message = "Last name is mandatory")
    @NotBlank(message = "Last name is mandatory")
    private String lastname;

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String dob;

    @NotNull(message = "Email name is mandatory")
    @NotBlank(message = "Email name is mandatory")
    @Email(message = "Email is not valid")
    private String email;



    @Override
    public String toString() {
        return "RegisterRequest{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dob=" + dob +
                ", email='" + email + '\'' +
                '}';
    }
}
