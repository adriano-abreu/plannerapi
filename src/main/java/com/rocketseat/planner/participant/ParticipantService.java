package com.rocketseat.planner.participant;

import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerParticipantsToEvent(List<String> participants, Trip trip) {
        List<Participant> participantsToRegister = participants.stream()
                .map(email -> new Participant(email, trip))
                .toList();


        participantRepository.saveAll(participantsToRegister);
        System.out.println("Participants to register: " + participantsToRegister.get(0).getId());
    };

    public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip) {
        Participant participant = new Participant(email, trip);
        participantRepository.save(participant);
        return new ParticipantCreateResponse(participant.getId());
    };

    public void triggerEmailsToParticipants(UUID tripId) {
        // Trigger emails to participants
    };

    public void triggerConfirmationEmailToParticipant(String email) {
        // Confirm participant
    };

    public List<ParticipantData> getParticipants(UUID tripId) {
        return participantRepository.findByTripId(tripId)
                .stream()
                .map(participant ->
                        new ParticipantData(
                                participant.getId(),
                                participant.getName(),
                                participant.getEmail(),
                                participant.getIsConfirmed()
                        ))
                .toList();
    };
}
