package com.fred.labplanner.service.planning.scheduling;

import java.time.LocalTime;

/**
 * Represents a reserved time interval in the scheduling system.
 *
 * <p>
 * A time slot defines a period between a start time and an end time.
 * It is used to represent occupied intervals for technicians,
 * laboratory equipment, maintenance windows, cleaning operations,
 * and other scheduling constraints.
 * </p>
 *
 * <p>
 * Time slots can be compared to detect overlapping reservations
 * and resource conflicts.
 * </p>
 */
public class TimeSlot {

    private LocalTime start;
    private LocalTime end;

    public TimeSlot() {
    }

    public TimeSlot(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    // ---------- CLASS METHODE ---------- //

    /**
     * Checks whether this time slot overlaps another time slot.
     *
     * <p>
     * Two time slots overlap when they share at least one common
     * instant in time.
     * </p>
     *
     * @param other time slot to compare with
     *
     * @return {@code true} if the two time slots overlap,
     * {@code false} otherwise
     */
    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end)
                && end.isAfter(other.start);
    }
}
