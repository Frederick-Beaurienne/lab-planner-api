package com.fred.labplanner.config;

import com.fred.labplanner.model.TimeSlot;

import java.time.LocalTime;

/**
 * Centralized laboratory default configuration.
 *
 * <p>
 * Contains global laboratory operating parameters used by the
 * scheduling engine and default resource initialization.
 * </p>
 */
public class LaboratoryDefaults {
    /**
     * Laboratory opening time.
     */
    public static final LocalTime LAB_OPENING_TIME =
            LocalTime.of(7, 0);

    /**
     * Laboratory closing time.
     */
    public static final LocalTime LAB_CLOSING_TIME =
            LocalTime.of(19, 0);

    /**
     * Default technician shift start time.
     */
    public static final LocalTime DEFAULT_TECHNICIAN_START_TIME =
            LocalTime.of(8, 0);

    /**
     * Default technician shift end time.
     */
    public static final LocalTime DEFAULT_TECHNICIAN_END_TIME =
            LocalTime.of(17, 0);

    /**
     * Default technician lunch break.
     */
    public static final TimeSlot DEFAULT_LUNCH_BREAK =
            TimeSlot.fromString("12:00-13:00");

    /**
     * Prevent utility class instantiation.
     */
    private LaboratoryDefaults() {
    }
}
