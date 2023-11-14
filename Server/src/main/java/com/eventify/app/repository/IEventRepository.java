package com.eventify.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventify.app.model.Event;
import com.eventify.app.model.User;
import com.eventify.app.model.enums.Categories;

public interface IEventRepository extends JpaRepository<Event, Long> {
    List<Event> findByTitleContaining(String keyword);
    Optional<Event> findByTitle(String title);
    List<Event> findByPlace(String place);
    List<Event> findByCategory(Categories category);
    List<Event> findByCreator(User creator);
    List<Event> findByParticipantsContaining(User user);
}


