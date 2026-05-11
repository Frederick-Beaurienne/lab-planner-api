package com.fred.labplanner.service.planning.metrics;

import com.fred.labplanner.model.Metrics;
import com.fred.labplanner.model.ScheduleEntry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
public class MetricsCalculator {


    // ---------- Public methods ---------- //

    /**
     * Calculates global scheduling metrics for the generated planning.
     *
     * <p>
     * The following metrics are calculated:
     * </p>
     *
     * <ul>
     *   <li><b>Total time</b> : duration between the first analysis start
     *   and the last analysis end</li>
     *
     *   <li><b>Efficiency</b> : percentage calculated using the formula:
     *   <br>
     *   {@code (total analysis duration / total planning duration) * 100}
     *   </li>
     *
     *   <li><b>Conflicts</b> : number of detected technician or equipment
     *   scheduling overlaps</li>
     * </ul>
     *
     * <p>
     * Efficiency may exceed 100% when multiple analyses are processed
     * in parallel.
     * </p>
     *
     * @param schedule generated planning schedule
     * @return calculated planning metrics
     */
    public Metrics calculateMetrics(List<ScheduleEntry> schedule) {

        // prevent NoSuchElementException on min/max operations
        if (schedule.isEmpty()) {
            return new Metrics(0, 0, 0);
        }

        // Total planning duration
        LocalTime firstStart = schedule.stream()
                .map(ScheduleEntry::getStartTime)
                .min(LocalTime::compareTo)
                .orElseThrow();

        LocalTime lastEnd = schedule.stream()
                .map(ScheduleEntry::getEndTime)
                .max(LocalTime::compareTo)
                .orElseThrow();

        long totalTime = Duration
                .between(firstStart, lastEnd)
                .toMinutes();

        /*
         * Sum of all analysis durations
         */
        long totalAnalysisTime = schedule.stream()
                .mapToLong(entry ->
                        Duration.between(
                                entry.getStartTime(),
                                entry.getEndTime()
                        ).toMinutes()
                )
                .sum();

        /*
         * Efficiency percentage
         */
        double efficiency = totalTime == 0 ? 0
                : Math.round(
                ((double) totalAnalysisTime / totalTime) * 10000
        ) / 100.0;

        /*
         * Conflict detection
         */
        int conflicts = countConflicts(schedule);

        return new Metrics(
                totalTime,
                efficiency,
                conflicts
        );
    }

    // ---------- Private helper methods ---------- //

    /**
     * Counts scheduling conflicts in the generated planning.
     *
     * <p>
     * A conflict occurs when two schedule entries overlap in time
     * while using the same technician or the same equipment.
     * </p>
     *
     * <p>
     * The method compares each schedule entry against subsequent entries
     * to detect overlapping resource usage.
     * </p>
     *
     * @param schedule generated planning schedule
     * @return number of detected conflicts
     */
    private int countConflicts(List<ScheduleEntry> schedule) {

        int conflicts = 0;

        // iterate through each schedule entry
        for (int i = 0; i < schedule.size(); i++) {

            ScheduleEntry current = schedule.get(i);

            // compare with each subsequent entry
            for (int j = i + 1; j < schedule.size(); j++) {

                ScheduleEntry other = schedule.get(j);

                boolean overlap =
                        current.getStartTime().isBefore(other.getEndTime())
                                &&
                                current.getEndTime().isAfter(other.getStartTime());

                // if entries overlap, check for technician and equipment conflicts
                if (overlap) {
                    // Technician conflict
                    if (
                            current.getTechnicianId()
                                    .equals(other.getTechnicianId())
                    ) {
                        conflicts++;
                    }

                    // Equipment conflict
                    if (
                            current.getEquipmentId()
                                    .equals(other.getEquipmentId())
                    ) {
                        conflicts++;
                    }
                }
            }
        }

        return conflicts;
    }

}
