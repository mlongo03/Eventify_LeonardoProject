package com.eventify.app.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.eventify.app.model.Event;
import com.eventify.app.model.User;
import com.eventify.app.model.enums.Categories;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FilterService {

    private final EventService eventService;
    private final UserService userService;

    public List<Event> findEventsByTitle(String search, List<Event> events) {
        List<Event> filteredEvents = new ArrayList<>();

        for (Event event : events) {
            if (event.getTitle().toLowerCase().contains(search.toLowerCase())) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    public List<Event> findEventsByPlace(String search, List<Event> events) {
        List<Event> filteredEvents = new ArrayList<>();
        String city = "";

        for (Event event : events) {
            String[] address = event.getPlace().split(" ");
            city = address[address.length - 2];
            if (city.toLowerCase().equals(search.toLowerCase())) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    public List<Event> findEventsByCategory(Categories[] categories, List<Event> events) {
        List<Event> filteredEvents = new ArrayList<>();

        for (Event event : events) {
            for (Categories category : categories) {
                if (event.getCategory() == category) {
                    filteredEvents.add(event);
                    break;
                }
            }
        }
        return filteredEvents;
    }

    public List<Event> findEventsByDateStart(String date, List<Event> events) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        date = date.replace("m", "");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            List<Event> filteredEvents = new ArrayList<>();
            for (Event event : events) {
                if (event.getDateTime().isAfter(dateTime)) {
                    filteredEvents.add(event);
                }
            }
            return filteredEvents;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date format is not valid : " + date, e);
        }
    }


    public List<Event> findEventsByDateEnd(String date, List<Event> events) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        date = date.replace("m", "");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            List<Event> filteredEvents = new ArrayList<>();

            for (Event event : events) {
                if (event.getDateTime().isBefore(dateTime)) {
                    filteredEvents.add(event);
                }
            }
            return filteredEvents;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date format is not valid : " + date, e);
        }
    }

    public List<Event> findEventsByDateInterval(String start, String end, List<Event> events) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        start = start.replace("m", "");
        end = end.replace("m", "");

        try {
            LocalDateTime startDateTime = LocalDateTime.parse(start, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);
            List<Event> filteredEvents = new ArrayList<>();

            for (Event event : events) {
                if (event.getDateTime().isAfter(startDateTime) && event.getDateTime().isBefore(endDateTime)) {
                    filteredEvents.add(event);
                }
            }
            return filteredEvents;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date format is not valid : " + start + " or " + end, e);
        }
    }

	public List<Event> findEventsByMyEvents(Long userId, List<Event> events) {
        Optional<User> userOptional = userService.getById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Event> createdEvents = eventService.getEventsCreatedByUser(user);
            return createdEvents;
        }
		return null;
	}

	public List<Event> findEventsByRegisteredEvents(Long userId, List<Event> events) {
        Optional<User> userOptional = userService.getById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Event> registeredEvents = eventService.getEventsRegisteredByUser(user);
            return registeredEvents;
        }
        return null;
	}

}
