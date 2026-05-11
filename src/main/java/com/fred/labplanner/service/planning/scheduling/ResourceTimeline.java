package com.fred.labplanner.service.planning.scheduling;

import java.time.LocalTime;
import java.util.ArrayList;
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
     * @param end requested end time
     *
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
     * @param end reservation end time
     */
    public void reserve(
            LocalTime start,
            LocalTime end
    ) {
        occupiedSlots.add(
                new TimeSlot(start, end)
        );
    }

    /**
     * Returns the next available time of the resource.
     *
     * <p>
     * The next available time corresponds to the end time of the
     * latest occupied slot in the timeline.
     * </p>
     *
     * <p>
     * If no occupied slot exists, {@link LocalTime#MIN} is returned.
     * </p>
     *
     * @return next available time for the resource
     */
    public LocalTime getNextAvailableTime() {

        return occupiedSlots.stream()
                .map(TimeSlot::getEnd)
                .max(LocalTime::compareTo)
                .orElse(LocalTime.MIN);
    }
}
