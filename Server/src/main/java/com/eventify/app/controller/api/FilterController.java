package com.eventify.app.controller.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eventify.app.model.Event;
import com.eventify.app.model.enums.Categories;
import com.eventify.app.model.json.FilterForm;
import com.eventify.app.model.json.ResponseGetEvents;
import com.eventify.app.service.EventService;
import com.eventify.app.service.FilterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;
    private final EventService eventService;

    @PostMapping("/filter/{userId}")
    public ResponseEntity<List<ResponseGetEvents>> findEventsByFilter(@PathVariable Long userId, @RequestBody FilterForm filterForm) {
        List<Event> events = eventService.getAllEvents();
        List<Event> filteredEvents = new ArrayList<>(events);

        if (filterForm.typeEventPage().equals("/my-events")) {
            filteredEvents.retainAll(filterService.findEventsByMyEvents(userId, events));
        }

        if (filterForm.typeEventPage().equals("/registered-events")) {
            filteredEvents.retainAll(filterService.findEventsByRegisteredEvents(userId, events));
        }

        if (filterForm.title() != null && !filterForm.title().isEmpty()) {
            filteredEvents.retainAll(filterService.findEventsByTitle(filterForm.title(), events));
        }

        if (filterForm.place() != null && !filterForm.place().isEmpty()) {
            filteredEvents.retainAll(filterService.findEventsByPlace(filterForm.place(), events));
        }

        if (filterForm.dateStart().length() != 0 && !filterForm.dateStart().isEmpty()) {
            filteredEvents.retainAll(filterService.findEventsByDateStart(filterForm.dateStart(), events));
        }

        if (filterForm.dateEnd() != null && !filterForm.dateEnd().isEmpty()) {
            filteredEvents.retainAll(filterService.findEventsByDateEnd(filterForm.dateEnd(), events));
        }

        if (filterForm.dateStart() != null && !filterForm.dateStart().isEmpty() && filterForm.dateEnd() != null && !filterForm.dateEnd().isEmpty()) {
            filteredEvents.retainAll(filterService.findEventsByDateInterval(filterForm.dateStart(), filterForm.dateEnd(), events));
        }

        if (filterForm.category() != null && filterForm.category().length > 0 && filterForm.category()[0] != Categories.EMPTY) {
            filteredEvents.retainAll(filterService.findEventsByCategory(filterForm.category(), events));
        }

        filteredEvents.sort(Comparator.comparing(Event::getDateTime));

        List<ResponseGetEvents> responseGetEvents = new ArrayList<>();
        List<Event> validEvents = new ArrayList<>();

        LocalDateTime currentDateTime = LocalDateTime.now();

        for (Event event : filteredEvents) {
            if (!event.getIsExpired() && !event.getDateTime().isBefore(currentDateTime)) {
                validEvents.add(event);
            } else {
                event.setExpired(true);
                eventService.updateEvent(event);
            }
        }

        for (Event event : validEvents) {
            responseGetEvents.add(new ResponseGetEvents(event.getId(), event.getTitle(),
                    event.getCategory().name(), event.getDescription(),
                    event.getPlace(), event.getDateTime().toString(),
                    "/api/download/" + event.getPhotos().get(0).getId()));
        }

        return ResponseEntity.ok(responseGetEvents);
    }
}
