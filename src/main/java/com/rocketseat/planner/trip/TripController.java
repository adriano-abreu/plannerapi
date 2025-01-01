package com.rocketseat.planner.trip;

import com.rocketseat.planner.activity.*;
import com.rocketseat.planner.participant.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService  activityService;

    @Autowired
    private TripRepository tripRepository;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        Trip newTrip = new Trip(payload);

        tripRepository.save(newTrip);
        participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId() ));
    }

    @GetMapping({"/{tripId}"})
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID tripId) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping({"/{tripId}"})
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID tripId, @RequestBody TripRequestPayload payload) {
        Optional<Trip> trip = tripRepository.findById(tripId);

        if (trip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Trip updatedTrip = trip.get();
        updatedTrip.setEndsAt(LocalDateTime.parse(payload.endsAt(), DateTimeFormatter.ISO_DATE_TIME));
        updatedTrip.setStartsAt(LocalDateTime.parse(payload.startsAt(), DateTimeFormatter.ISO_DATE_TIME));
        updatedTrip.setDestination(payload.destination());
        tripRepository.save(updatedTrip);
        return ResponseEntity.ok(updatedTrip);
    }

    @GetMapping({"/{tripId}/confirm"})
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID tripId) {
        Optional<Trip> trip = tripRepository.findById(tripId);

        if (trip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Trip updatedTrip = trip.get();
        updatedTrip.setIsConfirmed(true);

        tripRepository.save(updatedTrip);
        participantService.triggerEmailsToParticipants(tripId);

        return ResponseEntity.ok(updatedTrip);
    }

    @PostMapping({"/{tripId}/invite"})
    public ResponseEntity<ParticipantCreateResponse> inviteParticipants(@PathVariable UUID tripId, @RequestBody ParticipantRequestPayload payload) {
        Optional<Trip> trip = tripRepository.findById(tripId);

        if (trip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Trip updatedTrip = trip.get();

        ParticipantCreateResponse participantResponse = participantService.registerParticipantToEvent(payload.email(), updatedTrip);

        if (updatedTrip.getIsConfirmed()) {
            participantService.triggerConfirmationEmailToParticipant(payload.email());
        }

        return ResponseEntity.ok(participantResponse);
    }

    @GetMapping({"/{tripId}/participants"})
    public ResponseEntity<List<ParticipantData>> getParticipants(@PathVariable UUID tripId) {
        Optional<Trip> trip = tripRepository.findById(tripId);

        if (trip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(participantService.getParticipants(tripId));
    }

    @PostMapping({"/{tripId}/activities"})
    public ResponseEntity<ActivityResponse> registerActivities(@PathVariable UUID tripId, @RequestBody ActivityRequestPayload payload) {
        Optional<Trip> trip = tripRepository.findById(tripId);

        if (trip.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Trip updatedTrip = trip.get();

        ActivityResponse activityResponse = activityService.createActivity(payload, updatedTrip);

        return ResponseEntity.ok(activityResponse);
    }

    @GetMapping({"/{tripId}/activities"})
    public ResponseEntity<List<ActivityData>> getActivities(@PathVariable UUID tripId) {
        List<ActivityData> activity = activityService.getAllActivitiesFromTripId(tripId);

        if (activity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(activity);
    }
}
