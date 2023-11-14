package com.eventify.app.model;

import java.time.LocalDateTime;
import java.util.*;

import com.eventify.app.model.enums.Categories;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event", schema = "eventify")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(nullable = false)
    private String description;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateTime;

    @NotNull
    @Column(nullable = false)
    private String place;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private List<Photo> photos;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "event_participants",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> participants = new ArrayList<>();;

    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;

    @NotNull
    private Categories category;

    @NotNull
    private boolean isExpired;

    public Event(String title, String description, LocalDateTime dateTime, String place, List<Photo> photos, List<User> participants, User creator, Categories category) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.place = place;
        this.photos = photos;
        this.participants = participants;
        this.creator = creator;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public boolean getIsExpired() {
        return this.isExpired;
    }

    public void setExpired(boolean expired) {
        this.isExpired = expired;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public List<Photo> getPhotos() {
        return this.photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "YourClass{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateTime=" + dateTime +
                ", place='" + place + '\'' +
                ", category=" + category +
                '}';
    }
}
