package com.rocketseat.planner.activity;

import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public ActivityResponse createActivity(ActivityRequestPayload payload, Trip trip) {

        Activity newActivity = new Activity(payload.title(), payload.occursAt(), trip);

        activityRepository.save(newActivity);

        return new ActivityResponse(newActivity.getId());
    }

    public List<ActivityData> getAllActivitiesFromTripId(UUID tripId) {

        return activityRepository.findAllByTripId(tripId)
                .stream()
                .map(activity -> new ActivityData(
                        activity.getId(),
                        activity.getTitle(),
                        activity.getOccursAt())
                ).toList();
    }
}
