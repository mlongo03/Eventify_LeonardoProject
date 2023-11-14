package com.eventify.app.model.json;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class RegisterRequest {

  @NotNull(message = "First name is mandatory")
  @NotBlank(message = "First name is mandatory")
  private String firstname;
  @NotNull(message = "Last name is mandatory")
  @NotBlank(message = "Last name is mandatory")
  private String lastname;

  @NotNull
  @Temporal(TemporalType.DATE)
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date dob;

  @NotNull(message = "Email name is mandatory")
  @NotBlank(message = "Email name is mandatory")
  @Email(message = "Email is not valid")
  private String email;

  @NotNull(message = "Password name is mandatory")
  @NotBlank(message = "Password name is mandatory")
  @Size(min = 4, max = 40, message = "Password should be between 4 and 40 chars")
  private String password;

  @NotNull(message = "Confirm Password is mandatory")
  @NotBlank(message = "Confirm Password is mandatory")
  @Size(min = 4, max = 40, message = "Confirm Password should be between 4 and 40 chars")
  private String confirmPassword;

  private MultipartFile profilePicture;

  @AssertTrue(message = "You must accept the Terms and Conditions")
  private boolean checkbox;

  @Override
  public String toString() {
      return "RegisterRequest{" +
              "firstname='" + firstname + '\'' +
              ", lastname='" + lastname + '\'' +
              ", dob=" + dob +
              ", email='" + email + '\'' +
              ", password='" + password + '\'' +
              ", confirmPassword='" + confirmPassword + '\'' +
              ", profilePicture=" + profilePicture +
              ", checkbox=" + checkbox +
              '}';
  }
}
