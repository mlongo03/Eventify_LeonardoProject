package com.eventify.app.model.json;

import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import com.eventify.app.model.enums.Categories;

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
public class EventForm {
    @NotNull(message = "Title is mandatory")
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Description is mandatory")
    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "date is mandatory")
    @NotBlank(message = "date is mandatory")
    private String dateTime;

    @NotNull(message = "place is mandatory")
    @NotBlank(message = "place is mandatory")
    private String place;

    private List<MultipartFile> photos;

    private Categories category;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotNull(message = "date is mandatory") @NotBlank(message = "date is mandatory") String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(@NotNull(message = "date is mandatory") @NotBlank(message = "date is mandatory") String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPlace() {
        return this.place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public List<MultipartFile> getPhotos() {
        return this.photos;
    }

    public void setPhotos(List<MultipartFile> photos) {
        this.photos = photos;
    }

    public Categories getCategory() {
        return this.category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "YourClass{" +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateTime=" + dateTime +
                ", place='" + place + '\'' +
                ", category=" + category +
                '}';
    }
}
