package com.rocketseat.planner.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        Optional<Participant> participant = participantRepository.findById(id);

        if (participant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Participant updatedParticipant = participant.get();
        updatedParticipant.setIsConfirmed(true);
        updatedParticipant.setName(payload.name());
        participantRepository.save(updatedParticipant);
        return ResponseEntity.ok(updatedParticipant);
    }
}
