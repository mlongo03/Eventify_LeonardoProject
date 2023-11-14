package com.eventify.app.validator;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.util.IOUtils;
import com.eventify.app.model.User;
import com.eventify.app.model.json.RegisterRequest;

@Component
public class UserValidator {

    private static final String NAME_REGEX = "^[A-Za-z]+$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+)\\.[A-Za-z]{2,4}$";
    private static final byte[][] SUPPORTED_IMAGE_MAGIC_NUMBERS = {
        {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0},
        {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47}
    };

    public String isFormValid(RegisterRequest registerRequest) {
        if (registerRequest.getFirstname() == null || registerRequest.getFirstname().isEmpty() ||
            registerRequest.getLastname() == null || registerRequest.getLastname().isEmpty() ||
            registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty() ||
            registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty() ||
            registerRequest.getConfirmPassword() == null || registerRequest.getConfirmPassword().isEmpty() ||
            registerRequest.getDob() == null || registerRequest.getProfilePicture() == null || registerRequest.isCheckbox() == false
            ) {
            return "All fields must be filled";
        }
        Date dobDate = registerRequest.getDob();
        LocalDate dob = dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        Period age = Period.between(dob, today);
        if (!isValidName(registerRequest.getFirstname()) || !isValidName(registerRequest.getLastname())) {
            return "Firstname and lastname must contain only alphabetic characters";
        }
        if (!isImageFile(registerRequest.getProfilePicture())) {
            return "Invalid image file format. Only image files are allowed.";
        }
        if (!isValidEmail(registerRequest.getEmail())) {
            return "Invalid email address";
        }
        if (age.getYears() < 18) {
            return "You must have at least 18 years old";
        }
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return "Password are not equals";
        }
        if (registerRequest.getPassword().length() < 8)  {
            return "Password is less than 8 characters";
        }
        if (!isStrongPassword(registerRequest.getPassword())) {
            return "Password must contain at least : an uppercase char, a special character and a number";
        }
		return null;
	}

    public String isFormValid(User user, MultipartFile imageFile, String confirmPassword) {
        if (user.getFirstname() == null || user.getFirstname().isEmpty() ||
            user.getLastname() == null || user.getLastname().isEmpty() ||
            user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty() ||
            user.getDob() == null || imageFile == null || confirmPassword == null) {
            return "All fields must be filled";
        }
        Date dobDate = user.getDob();
        LocalDate dob = dobDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        Period age = Period.between(dob, today);
        if (!isValidName(user.getFirstname()) || !isValidName(user.getLastname())) {
            return "Firstname and lastname must contain only alphabetic characters";
        }
        if (!isImageFile(imageFile)) {
            return "Invalid image file format. Only image files are allowed.";
        }
        if (!isValidEmail(user.getEmail())) {
            return "Invalid email address";
        }
        if (age.getYears() < 18) {
            return "You must have at least 18 years old";
        }
        if (!user.getPassword().equals(confirmPassword)) {
            return "Password are not equals";
        }
        if (user.getPassword().length() < 8)  {
            return "Password is less than 8 characters";
        }
        if (!isStrongPassword(user.getPassword())) {
            return "Password must contain at least : an uppercase char, a special character and a number";
        }
		return null;
	}

    public boolean isValidName(String name) {
        return name.matches(NAME_REGEX);
    }

    public boolean isStrongPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    public boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    public boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        try {
            byte[] fileBytes = IOUtils.toByteArray(file.getInputStream());
            for (byte[] magicNumber : SUPPORTED_IMAGE_MAGIC_NUMBERS) {
                if (startsWith(fileBytes, magicNumber)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean startsWith(byte[] array, byte[] prefix) {
        if (array.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (array[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }
}
