package com.fred.labplanner.service.planning.scheduling;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceTimelineTest {

    @Test
    void shouldSkipOccupiedSlot() {

        ResourceTimeline timeline = new ResourceTimeline();

        // occupied slot
        timeline.reserve(
                LocalTime.of(12, 0),
                LocalTime.of(13, 0)
        );

        LocalTime nextAvailable =
                timeline.getNextAvailableTime(
                        LocalTime.of(12, 15),
                        30
                );

        assertEquals(
                LocalTime.of(13, 0),
                nextAvailable
        );
    }
}