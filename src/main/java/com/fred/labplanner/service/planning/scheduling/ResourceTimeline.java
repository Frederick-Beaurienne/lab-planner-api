package com.fred.labplanner.service.planning.scheduling;

import com.fred.labplanner.model.TimeSlot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents the reservation timeline of a scheduling resource.
 *
 * <p>
 * A resource timeline stores all occupied time slots associated with
 * a laboratory resource such as a technician or a piece of equipment.
 * </p>
 *
 * <p>
 * This class is used by the scheduling engine to:
 * </p>
 *
 * <ul>
 *   <li>Check resource availability</li>
 *   <li>Reserve analysis time slots</li>
 *   <li>Track occupied periods</li>
 *   <li>Determine the next available time</li>
 * </ul>
 *
 * <p>
 * Occupied slots may represent:
 * </p>
 *
 * <ul>
 *   <li>Laboratory analyses</li>
 *   <li>Lunch breaks</li>
 *   <li>Equipment maintenance</li>
 *   <li>Cleaning operations</li>
 * </ul>
 */
public class ResourceTimeline {

    private final List<TimeSlot> occupiedSlots = new ArrayList<>();


    public List<TimeSlot> getOccupiedSlots() {
        return occupiedSlots;
    }

    // ---------- CLASS METHODE ---------- //

    /**
     * Checks whether the resource is available during the specified time slot.
     *
     * <p>
     * A resource is considered available if the requested time slot
     * does not overlap any existing occupied slot in the timeline.
     * </p>
     *
     * @param start requested start time
     * @param end   requested end time
     * @return {@code true} if the resource is available during the
     * specified period, {@code false} otherwise
     */
    public boolean isAvailable(
            LocalTime start,
            LocalTime end
    ) {

        TimeSlot candidate = new TimeSlot(start, end);

        return occupiedSlots.stream()
                .noneMatch(slot -> slot.overlaps(candidate));
    }

    /**
     * Reserves the specified time slot in the resource timeline.
     *
     * <p>
     * The reserved slot is added to the list of occupied periods
     * for the resource.
     * </p>
     *
     * @param start reservation start time
     * @param end   reservation end time
     */
    public void reserve(
            LocalTime start,
            LocalTime end
    ) {
        occupiedSlots.add(
                new TimeSlot(start, end)
        );

        // ensure order
        occupiedSlots.sort(
                Comparator.comparing(TimeSlot::getStart)
        );
    }

    public LocalTime getNextAvailableTime(
            LocalTime requestedStart,
            long durationMinutes
    ) {

        LocalTime candidateStart = requestedStart;

        for (TimeSlot slot : occupiedSlots) {

            LocalTime candidateEnd =
                    candidateStart.plusMinutes(durationMinutes);

            TimeSlot candidate =
                    new TimeSlot(
                            candidateStart,
                            candidateEnd
                    );

            if (slot.overlaps(candidate)) {

                candidateStart = slot.getEnd();
            }
        }

        return candidateStart;
    }
}
