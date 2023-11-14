package com.eventify.app.controller.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventify.app.model.Event;
import com.eventify.app.model.Photo;
import com.eventify.app.model.User;
import com.eventify.app.model.enums.Categories;
import com.eventify.app.model.json.EventForm;
import com.eventify.app.model.json.ParticipantsResponse;
import com.eventify.app.model.json.ResponseGetEvent;
import com.eventify.app.model.json.ResponseGetEvents;
import com.eventify.app.repository.IEventRepository;
import com.eventify.app.service.EmailService;
import com.eventify.app.service.EventReminderScheduler;
import com.eventify.app.service.EventService;
import com.eventify.app.service.GeocodingService;
import com.eventify.app.service.NotificationService;
import com.eventify.app.service.PhotoService;
import com.eventify.app.service.UserService;
import com.eventify.app.validator.EventValidator;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final IEventRepository eventRepository;
    private final EmailService emailService;
    private final EventService eventService;
    private final UserService userService;
    private final EventValidator eventValidator;
    private final PhotoService photoService;
    private final GeocodingService geocodingService;
    private final NotificationService notificationService;
    private final EventReminderScheduler eventReminderScheduler;

    @PostMapping("/api/create-event/{userId}")
    public ResponseEntity<String> createEvent(@PathVariable Long userId,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("dateTime") String dateTime,
        @RequestParam("place") String place,
        @RequestParam("category") Categories category,
        @RequestParam("photos") List<MultipartFile> photos) {

        EventForm eventRequest = EventForm.builder()
        .category(category)
        .dateTime(dateTime)
        .description(description)
        .place(place)
        .title(title)
        .photos(photos)
        .build();

        try {
            Optional<User> user = userService.getById(userId);
            List<Object> response = eventService.createEvent(userId, eventRequest);
            emailService.sendCreationEventConfirm(user.get().getEmail(), eventRequest.getTitle());

            return ResponseEntity.status(HttpStatus.OK).body((String)response.get(0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating the event: " + e.getMessage());
        }
    }

    @GetMapping("/api/event/findById/{eventId}")
    public ResponseEntity<ResponseGetEvent> findEventbyId(@PathVariable Long eventId) {

        Optional<Event> event = eventRepository.findById(eventId);

        if (event.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        List<String> images = new ArrayList<>();

        for (int i = 0; i < event.get().getPhotos().size(); i++) {
            images.add("/api/download/" + event.get().getPhotos().get(i).getId());
        }

        List<ParticipantsResponse> participants = new ArrayList<>();
        List<User> participant = event.get().getParticipants();

        for (int i = 0; i < participant.size(); i++) {
            participants.add(ParticipantsResponse.builder()
            .name(participant.get(i).getFirstname() + " " + participant.get(i).getLastname())
            .id(participant.get(i).getId())
            .build());
        }

        ResponseGetEvent responseGetEvent = ResponseGetEvent.builder()
        .id(event.get().getId())
        .title(event.get().getTitle())
        .address(event.get().getPlace())
        .category(event.get().getCategory().name())
        .date(event.get().getDateTime().toString())
        .description(event.get().getDescription())
        .imageURL(images)
        .participants(participants)
        .build();

        return ResponseEntity.ok(responseGetEvent);
    }

    @GetMapping("/api/user/all-events")
    public ResponseEntity<List<ResponseGetEvents>> getAllEvents() {

            List<ResponseGetEvents> responseGetEvents = new ArrayList<>();
            List<Event> events = eventService.getAllEvents();

            events.sort(Comparator.comparing(Event::getDateTime));

            List<Event> validEvents = new ArrayList<>();
            LocalDateTime currentDateTime = LocalDateTime.now();

            for (Event event : events) {
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
                    event.getPlace(), event.getDateTime().toString()
                    , "/api/download/" + event.getPhotos().get(0).getId()));
                }
            return ResponseEntity.ok(responseGetEvents);
    }

    @DeleteMapping("/api/event/{eventId}/delete-event/{userId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId, @PathVariable Long userId) throws JsonProcessingException {
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        Optional<User> userOptional = userService.getById(userId);

        if (eventOptional.isPresent() && userOptional.isPresent()) {
            Event event = eventOptional.get();
            List<User> participants = event.getParticipants();
            String title = event.getTitle();
            User user = userOptional.get();
            if (event.getCreator().getId().equals(userId)) {
                eventReminderScheduler.unscheduleEventReminders(event);
                eventService.deletePhotosByEvent(eventService.getEventById(eventId).get());
                eventService.deleteEventById(eventId);
                try {
                    notificationService.createNotification(userId, null, "Event correctly deleted, named : ", title);
                    notificationService.sendNotificationToUserId(userId);
                    for (User participant: participants) {
                        notificationService.createNotification(participant.getId(), null, "The event you were subscribed has been deleted, named : ", title);
                        notificationService.sendNotificationToUserId(participant.getId());
                    }
                    emailService.sendEventAbortedAdvice(user.getEmail(), event.getTitle());
                    return ResponseEntity.ok("All utents and the event deleted.");
                } catch (MessagingException e) {
                    return ResponseEntity.badRequest().body("Mail not sent.");
                }
            } else {
                return ResponseEntity.badRequest().body("User isn't the creator.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/event/showEventMap")
    public ResponseEntity<String> showEventMap(@RequestParam("address") String eventAddress, Model model) {
        double[] coordinates = geocodingService.getCoordinatesFromAddress(eventAddress);
        if (coordinates != null) {
            double latitude = coordinates[0];
            double longitude = coordinates[1];

            model.addAttribute("EVENT_LATITUDE", latitude);
            model.addAttribute("EVENT_LONGITUDE", longitude);

            return ResponseEntity.ok("Localization of the event available.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coordinates not found.");
        }
    }

    @GetMapping("/api/event/findBy-title")
    public ResponseEntity<Optional<Event>> findEventsByTitle(@RequestParam String title) {
        Optional<Event> event = eventService.findByTitle(title);
        if (!event.isEmpty()) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/event/findBy-place")
    public ResponseEntity<List<Event>> findEventsByPlace(@RequestParam String place) {
        List<Event> events = eventService.findEventsByPlace(place);
        if (!events.isEmpty()) {
            return ResponseEntity.ok(events);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/api/event/findBy-category")
    public ResponseEntity<List<Event>> findEventsByCategory(@RequestParam Categories category) {
        List<Event> events = eventService.findEventsByCategory(category);
        if (!events.isEmpty()) {
            return ResponseEntity.ok(events);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/api/event/{eventId}/register/{userId}")
    public ResponseEntity<String> registerForEvent(@PathVariable Long eventId, @PathVariable Long userId) throws JsonProcessingException {
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        Optional<User> userOptional = userService.getById(userId);

        if (eventOptional.isPresent() && userOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();
            List<User> participants = event.getParticipants();

            participants.add(user);
            event.setParticipants(participants);
            eventService.updateEvent(event);
            eventReminderScheduler.scheduleEventReminders(eventService.getEventById(eventId).get());

            try {
                notificationService.createNotification(userId, eventId, "Correctly subscribed for the event! ", null);
                notificationService.sendNotificationToUserId(userId);
                notificationService.createNotification(userId, eventId, "Subscribed for creator", null);
                notificationService.sendNotificationToUserId(eventService.getEventById(eventId).get().getCreator().getId());
                emailService.sendRegisterEventConfirm(user.getEmail(), event.getTitle());
            }  catch (MessagingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mail not sent.");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body("User is registered for the event.");
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/api/event/{eventId}/unregister/{userId}")
    public ResponseEntity<String> unregisterFromEvent(@PathVariable Long eventId, @PathVariable Long userId) throws JsonProcessingException {
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        Optional<User> userOptional = userService.getById(userId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();
            List<User> participants = event.getParticipants();

            if (participants.size() != 0) {
                boolean removed = participants.removeIf(participant -> participant.getId().equals(userId));

                if (removed) {
                    event.setParticipants(participants);
                    eventService.updateEvent(event);
                    eventReminderScheduler.unscheduleEventReminders(event);

                    try {
                        notificationService.createNotification(userId, eventId, "Correctly unsubscribed for the event! ", null);
                        notificationService.sendNotificationToUserId(userId);
                        notificationService.createNotification(userId, eventId, "Unsubscribed for creator", null);
                        notificationService.sendNotificationToUserId(eventService.getEventById(eventId).get().getCreator().getId());
                        emailService.sendUnregisterEventConfirm(user.getEmail(), event.getTitle());
                    }  catch (MessagingException e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mail not sent.");
                    }
                    return ResponseEntity.ok("User has been unregistered for the event");
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User isn't registered for the event.");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/event/{eventId}/participants")
    public ResponseEntity<List<ParticipantsResponse>> getEventParticipants(@PathVariable Long eventId) {
        Optional<Event> eventOptional = eventService.getEventById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            List<User> participants = event.getParticipants();
            if (participants.size() == 0) {
                return ResponseEntity.ok(null);
            }
            List<ParticipantsResponse> names = new ArrayList<>();
            for (User user: participants) {
                names.add(ParticipantsResponse.builder()
                .name(user.getFirstname() + " " + user.getLastname())
                .id(user.getId())
                .build());
            }
            return ResponseEntity.ok(names);
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/api/user/{userId}/created-events")
    public ResponseEntity<List<Event>> getCreatedEvents(@PathVariable Long userId) {
        Optional<User> userOptional = userService.getById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Event> createdEvents = eventService.getEventsCreatedByUser(user);
            return ResponseEntity.ok(createdEvents);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/user/{userId}/registered-events")
    public ResponseEntity<List<Event>> getRegisteredEvents(@PathVariable Long userId) {
        Optional<User> userOptional = userService.getById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Event> registeredEvents = eventService.getEventsRegisteredByUser(user);
            return ResponseEntity.ok(registeredEvents);
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/api/event/{eventId}/modify-event/{userId}")
    public ResponseEntity<String> modifyEvent(@PathVariable Long eventId, @PathVariable Long userId,
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("dateTime") String dateTime,
        @RequestParam("place") String place,
        @RequestParam("category") Categories category,
        @RequestParam("photos") List<MultipartFile> photos) {
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        Optional<User> userOptional = userService.getById(userId);

        EventForm eventRequest = EventForm.builder()
        .category(category)
        .dateTime(dateTime)
        .description(description)
        .place(place)
        .title(title)
        .photos(photos)
        .build();


        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();
            if (event.getCreator().equals(user)) {
                String check = eventValidator.isFormValid(eventRequest);
                eventReminderScheduler.unscheduleEventReminders(event);
                if (check == null) {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

                    try {
                        LocalDateTime dateTimeUpdate = LocalDateTime.parse(dateTime, formatter);
                        Event eventToUpdate = eventService.getEventById(eventId).get();
                        eventToUpdate.setCategory(category);
                        eventToUpdate.setDateTime(dateTimeUpdate);
                        eventToUpdate.setDescription(description);
                        eventToUpdate.setTitle(title);
                        eventService.deletePhotosByEvent(eventToUpdate);
                        for (MultipartFile photo : photos) {
                            Photo pic = photoService.uploadPhoto(photo);
                            pic.setEvent(eventToUpdate);
                            photoService.create(pic);

                            if (eventToUpdate.getPhotos() == null) {
                                eventToUpdate.setPhotos(new ArrayList<>());
                            }
                            eventToUpdate.getPhotos().add(pic);
                        }
                        eventService.updateEvent(eventToUpdate);
                        eventReminderScheduler.scheduleEventReminders(event);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                else
                    return ResponseEntity.ok("Not valid updates " + check);
                try {
                    for (User participant : event.getParticipants()) {
                        emailService.sendChangesAdviseAboutEvent(participant.getEmail(), event.getTitle());
                        notificationService.createNotification(userId, eventId, "Updated event named : ", null);
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sending email error. Sending email error");
                }
                return ResponseEntity.ok("Event succesfully modified.");
            } else
                return ResponseEntity.ok("User not recognized as creator.");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/api/event/{eventId}/change-title/{userId}")
    public ResponseEntity<String> changeEventTitle(@PathVariable Long eventId, @PathVariable Long userId, @RequestParam String newTitle) {
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        Optional<User> userOptional = userService.getById(userId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();
            if (event.getCreator().equals(user)) {
                event.setTitle(newTitle);
                eventService.updateEvent(event);

                try {
                    for (User participant : event.getParticipants()) {
                        emailService.sendChangesAdviseAboutEvent(participant.getEmail(), event.getTitle());
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sending email error");
                }
                return ResponseEntity.ok("Title of event succesfully modified.");
            } else
                return ResponseEntity.ok("User not recognized as creator.");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/api/event/{eventId}/change-description/{userId}")
    public ResponseEntity<String> changeEventDescription(@PathVariable Long eventId, @PathVariable Long userId, @RequestParam String newDescription) {
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        Optional<User> userOptional = userService.getById(userId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();
            if (event.getCreator().equals(user)) {
                event.setDescription(newDescription);
                eventService.updateEvent(event);

                try {
                    for (User participant : event.getParticipants()) {
                        emailService.sendChangesAdviseAboutEvent(participant.getEmail(), event.getTitle());
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sending email error");
                }
                return ResponseEntity.ok("Event description succesfully modified.");
            } else
                return ResponseEntity.ok("User not recognized as creator.");

        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/api/event/{eventId}/change-date-time/{userId}")
    public ResponseEntity<String> changeEventDateTime(@PathVariable Long eventId, @PathVariable Long userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime) {
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        Optional<User> userOptional = userService.getById(userId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();
            if (event.getCreator().equals(user)) {
                event.setDateTime(newDateTime);
                eventService.updateEvent(event);

                try {
                    for (User participant : event.getParticipants()) {
                        emailService.sendChangesAdviseAboutEvent(participant.getEmail(), event.getTitle());
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sending email error");
                }
                return ResponseEntity.ok("Date e time succesfully modified.");
            } else
                return ResponseEntity.ok("User not recognized as creator.");

        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/api/event/{eventId}/change-place/{userId}")
    public ResponseEntity<String> changeEventPlace(@PathVariable Long eventId, @PathVariable Long userId, @RequestParam String place) {
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        Optional<User> userOptional = userService.getById(userId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();
            if (event.getCreator().equals(user)) {
                event.setPlace(place);
                eventService.updateEvent(event);

                try {
                    for (User participant : event.getParticipants()) {
                        emailService.sendChangesAdviseAboutEvent(participant.getEmail(), event.getTitle());
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sending email error");
                }
                return ResponseEntity.ok("New place for event succesfully updated");
            } else
                return ResponseEntity.ok("User not recognized as creator.");

        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/api/event/{eventId}/change-category/{userId}")
    public ResponseEntity<String> changeEventCategory(@PathVariable Long eventId, @PathVariable Long userId, @RequestParam Categories newCategory) {
        Optional<Event> eventOptional = eventService.getEventById(eventId);
        Optional<User> userOptional = userService.getById(userId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            User user = userOptional.get();
            if (event.getCreator().equals(user)) {
                event.setCategory(newCategory);
                eventService.updateEvent(event);

                try {
                    for (User participant : event.getParticipants()) {
                        emailService.sendChangesAdviseAboutEvent(participant.getEmail(), event.getTitle());
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sending email error");
                }
                return ResponseEntity.ok("Event category succesfully modified.");
            } else
                return ResponseEntity.ok("User not recognized as creator.");

        }
        return ResponseEntity.notFound().build();
    }
}
