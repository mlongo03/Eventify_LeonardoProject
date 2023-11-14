package com.eventify.app.validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.util.IOUtils;
import com.eventify.app.model.json.EventForm;

@Component
public class EventValidator {

    private static final String NAME_REGEX = "^[A-Za-z]+$";
    private static final byte[][] SUPPORTED_IMAGE_MAGIC_NUMBERS = {
        {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0},
        {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47}
    };


    public String isFormValid(EventForm event) {
        if (event.getTitle() == null || event.getTitle().isEmpty() ||
            event.getDescription() == null || event.getDescription().isEmpty() ||
            event.getPlace() == null || event.getPlace().isEmpty() ||
            event.getTitle().isEmpty() || event.getCategory() == null ||
            event.getPhotos().isEmpty() || event.getDateTime() == null)
        {
            return "All fields must be filled";
        }
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            LocalDateTime dateTime = LocalDateTime.parse(event.getDateTime(), formatter);

            if (dateTime.isBefore(currentTime.plus(24, ChronoUnit.HOURS))) {
                return "Event date and time must be at least 24 hours from now";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Invalid date";
        }

        for (MultipartFile photo : event.getPhotos()) {
            if (!isImageFile(photo)) {
                return "Invalid image file format in event photos. Only image files are allowed.";
            }
        }
        return null;
	}

    public boolean isValidName(String name) {
        return name.matches(NAME_REGEX);
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
