package com.eventify.app.controller.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventify.app.model.Photo;
import com.eventify.app.model.User;
import com.eventify.app.model.json.ResponseProfileInfo;
import com.eventify.app.service.AuthService;
import com.eventify.app.service.EmailService;
import com.eventify.app.service.NotificationService;
import com.eventify.app.service.PhotoService;
import com.eventify.app.service.UserService;
import com.eventify.app.validator.UserValidator;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class UserController {

	private final UserService userService;
	private final EmailService emailService;
	private final PhotoService photoService;
	private final NotificationService notificationService;
	private final UserValidator userValidator;
	private final AuthService authService;

	@PutMapping("/api/confirm-email")
    public ResponseEntity<String> confirmEmailChange(@PathVariable Long userId, @RequestParam String firstname, @RequestParam String lastname, @RequestParam String email ,@RequestParam String birth , @RequestParam MultipartFile pics, @RequestParam String otpCode) throws Exception {
        Optional<User> userOptional = userService.getById(userId);
        int otp = Integer.parseInt(otpCode);
        User user = userOptional.get();

        if (user.getOtp() == otp) {
            if (firstname != null)
                user.setFirstname(firstname);
            if (lastname != null)
                user.setLastname(lastname);
            if (birth != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = formatter.parse(birth);
                    user.setDob(date);
                } catch (ParseException e){
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error formatting the date of birth.");
                }
            }
            if (pics != null) {
                try {
                    Photo profilePics = photoService.uploadPhoto(pics);
                    user.setProfilePicture(profilePics);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            userService.update(userId, user);
            try {
                emailService.sendChangesAdviseAboutProfile(email);
                notificationService.createNotification(userId, null, "Your datas has been updated.", null);
            } catch (MessagingException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email.");
            }
            return ResponseEntity.ok("Profile succesfully updated.");
        }
        return ResponseEntity.ok("Wrong otp code.");
    }

    @GetMapping("/api/getProfileInfo/{userId}")
    public ResponseEntity<ResponseProfileInfo> getProfileInfo(@PathVariable Long userId) {
        Optional<User> user = userService.getById(userId);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(ResponseProfileInfo.builder()
        .date(user.get().getDob().toString())
        .email(user.get().getEmail())
        .firstName(user.get().getFirstname())
        .lastName(user.get().getLastname())
        .imageUrl("/api/download/" + user.get().getProfilePicture().getId())
        .build());
    }

    @PutMapping("/api/modify-profile/{userId}")
    public ResponseEntity<String> modifyProfileMultipart(@PathVariable Long userId, @RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname, @RequestParam("email") String email, @RequestParam("profilePhoto") MultipartFile pics) throws Exception {
        Optional<User> userOptional = userService.getById(userId);

        System.out.println("modify profile credentials" + email + email.length() + firstname + firstname.length() + lastname + lastname.length() + "\n\n\n");
        if (userOptional.isPresent()) {

            if (firstname.length() != 0) {
                if (!userValidator.isValidName(firstname))
                    return ResponseEntity.ok("Firstname of user not valid.");
            }
            if (lastname.length() != 0) {
                if (!userValidator.isValidName(lastname))
                    return ResponseEntity.ok("Lastname of user not valid.");
            }
            if (pics != null) {
                if (!userValidator.isImageFile(pics))
                    return ResponseEntity.ok("Image not PNG, JPG or JPEG");
            }
            if (email.length() == 0) {
                User user = userOptional.get();
                if (firstname.length() != 0)
                    user.setFirstname(firstname);
                if (lastname.length() != 0)
                    user.setLastname(lastname);
                if (pics != null) {
                    try {
                        Photo profilePics = photoService.uploadPhoto(pics);
                        photoService.create(profilePics);
                        user.setProfilePicture(profilePics);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
                userService.update(userId, user);
                try {
                    emailService.sendChangesAdviseAboutProfile(user.getEmail());
                	notificationService.createNotification(userId, null, "Your has been updated", null);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email.");
                }
                return ResponseEntity.ok("Profile succesfully updated.");
            } else {
                if (!userValidator.isValidEmail(email))
                    return ResponseEntity.ok("Email insert is not valid.");
                String secretKey = authService.generateSecretKey();
                int otp = authService.generateOtp(secretKey);
                User user = userOptional.get();
                user.setOtp(otp);
                try {
                    emailService.sendConfirmChangeEmail(email, otp);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email.");
                }
                return ResponseEntity.ok("Email isn't let validated.");
            }
        }
        return ResponseEntity.ok("User not found.");
    }

    @PutMapping("/api/modify-profile-multipart/{userId}")
    public ResponseEntity<String> modifyProfile(@PathVariable Long userId, @RequestParam("firstname") String firstname, @RequestParam("lastname") String lastname, @RequestParam("email") String email) throws Exception {
        Optional<User> userOptional = userService.getById(userId);

        System.out.println("modify profile credentials" + email + email.length() + firstname + firstname.length() + lastname + lastname.length() + "\n\n\n");
        if (userOptional.isPresent()) {

            if (firstname.length() != 0) {
                if (!userValidator.isValidName(firstname))
                    return ResponseEntity.ok("Firstname of user not valid.");
            }
            if (lastname.length() != 0) {
                if (!userValidator.isValidName(lastname))
                    return ResponseEntity.ok("Lastname of user not valid.");
            }
            if (email.length() == 0) {
                User user = userOptional.get();
                if (firstname.length() != 0)
                    user.setFirstname(firstname);
                if (lastname.length() != 0)
                    user.setLastname(lastname);
                userService.update(userId, user);
                try {
                    emailService.sendChangesAdviseAboutProfile(user.getEmail());
                	notificationService.createNotification(userId, null, "Your has been updated", null);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email.");
                }
                return ResponseEntity.ok("Profile succesfully updated.");
            } else {
                if (!userValidator.isValidEmail(email))
                    return ResponseEntity.ok("Email insert is not valid.");
                String secretKey = authService.generateSecretKey();
                int otp = authService.generateOtp(secretKey);
                User user = userOptional.get();
                user.setOtp(otp);
                try {
                    emailService.sendConfirmChangeEmail(email, otp);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email.");
                }
                return ResponseEntity.ok("Email isn't let validated.");
            }
        }
        return ResponseEntity.ok("User not found.");
    }
}
