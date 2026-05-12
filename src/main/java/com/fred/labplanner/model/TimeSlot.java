package com.fred.labplanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fred.labplanner.json.deserializer.TimeSlotDeserializer;
import com.fred.labplanner.json.serializer.TimeSlotSerializer;

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
@JsonSerialize(using = TimeSlotSerializer.class)
public class TimeSlot {

    private LocalTime start;
    private LocalTime end;

    // ---------- CONSTRUCTORS ---------- //

    public TimeSlot() {
    }

    public TimeSlot(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    // ---------- GETTERS AND SETTERS ---------- //

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    // ---------- TO STRING ---------- //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(start);
        sb.append("-").append(end);
        return sb.toString();
    }

    // ---------- CLASS METHODE ---------- //

    @JsonCreator
    public static TimeSlot fromString(String value) {

        return TimeSlotDeserializer.deserialize(value);
    }

    /**
     * Checks whether this time slot overlaps another time slot.
     *
     * <p>
     * Two time slots overlap when they share at least one common
     * instant in time.
     * </p>
     *
     * @param other time slot to compare with
     * @return {@code true} if the two time slots overlap,
     * {@code false} otherwise
     */
    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end)
                && end.isAfter(other.start);
    }
}
