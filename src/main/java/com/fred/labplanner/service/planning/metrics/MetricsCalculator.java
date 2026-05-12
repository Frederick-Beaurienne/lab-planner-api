package com.fred.labplanner.service.planning.metrics;

import com.fred.labplanner.model.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
public class MetricsCalculator {


    // ---------- Public methods ---------- //

    public Metrics calculateMetrics(List<ScheduleEntry> schedule,
                                    List<Technician> technicians,
                                    List<Sample> samples) {
        Metrics metrics = new Metrics();

        // prevent NoSuchElementException on min/max operations
        if (schedule.isEmpty()) {
            return metrics;
        }

        // Total planning duration
        totalTimeCalculation(metrics, schedule);

        // Efficiency percentage
        efficiencyCalculation(metrics, schedule, technicians);

        // parallelism rate
        parallelismRateCalculation(metrics, schedule);

        // Conflict detection
        countConflicts(metrics, schedule);

        // averageWaitTime
        averageWaitTimeCalculation(metrics, schedule, samples);

        // technician tilization
        technicianUtilizationCalculation(metrics, schedule, technicians);

        // lunch interruptions
        lunchInterruptionsCalculation(metrics, schedule, technicians);

        return metrics;
    }

    // ---------- Private helper methods ---------- //

    private void totalTimeCalculation(
            Metrics metrics,
            List<ScheduleEntry> schedule) {
        // Total planning duration
        LocalTime firstStart = schedule.stream()
                .map(ScheduleEntry::getStartTime)
                .min(LocalTime::compareTo)
                .orElseThrow();

        LocalTime lastEnd = schedule.stream()
                .map(ScheduleEntry::getEndTime)
                .max(LocalTime::compareTo)
                .orElseThrow();

        metrics.setTotalTime(Duration
                .between(firstStart, lastEnd)
                .toMinutes());
    }

    private void efficiencyCalculation(
            Metrics metrics,
            List<ScheduleEntry> schedule,
            List<Technician> technicians) {

        long totalTime = metrics.getTotalTime();
        int technicianCount = technicians.size();

        // Sum of all analysis durations
        long totalOccupationTime = schedule.stream()
                .mapToLong(ScheduleEntry::getDuration)
                .sum();

        if (totalTime == 0 || technicianCount == 0) {
            metrics.setEfficiency(0.0);
            return;
        }

        double efficiency = ((double) totalOccupationTime
                / technicianCount
                / totalTime
        ) * 100;

        metrics.setEfficiency(Math.round(efficiency * 100) / 100.0);
    }

    private void parallelismRateCalculation(Metrics metrics, List<ScheduleEntry> schedule) {
        long totalTime = metrics.getTotalTime();

        if (totalTime == 0) {

            metrics.setParallelismRate(0.0);

            return;
        }

        long totalOccupationTime = schedule.stream()
                .mapToLong(ScheduleEntry::getDuration)
                .sum();

        double parallelismRate =
                (double) totalOccupationTime
                        / totalTime;

        metrics.setParallelismRate(
                Math.round(parallelismRate * 100) / 100.0
        );
    }

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
     */
    private void countConflicts(
            Metrics metrics,
            List<ScheduleEntry> schedule
    ) {

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

        metrics.setConflicts(conflicts);
    }

    private void averageWaitTimeCalculation(
            Metrics metrics,
            List<ScheduleEntry> schedule,
            List<Sample> samples
    ) {

        long totalWaitTime = 0;

        int processedSamplesCount = 0;

        for (ScheduleEntry entry : schedule) {

            Sample sample = samples.stream()
                    .filter(s -> s.getId().equals(entry.getId()))
                    .findFirst()
                    .orElse(null);

            if (sample != null) {
                long waitTime = Duration.between(
                        sample.getArrivalTime(),
                        entry.getStartTime()
                ).toMinutes();

                totalWaitTime += Math.max(0, waitTime);

                processedSamplesCount++;
            }
        }

        if (processedSamplesCount == 0) {
            metrics.setAverageWaitTime(0L);

            return;
        }

        metrics.setAverageWaitTime(totalWaitTime / processedSamplesCount);
    }

    private void technicianUtilizationCalculation(
            Metrics metrics,
            List<ScheduleEntry> schedule,
            List<Technician> technicians
    ) {

        long totalWorkedTime = schedule.stream()
                .mapToLong(ScheduleEntry::getDuration)
                .sum();

        long totalAvailableTime = technicians.stream()
                .mapToLong(t ->
                        Duration.between(
                                t.getStartTime(),
                                t.getEndTime()
                        ).toMinutes()
                )
                .sum();

        if (totalAvailableTime == 0) {

            metrics.setTechnicianUtilization(0.0);

            return;
        }

        double utilization =
                ((double) totalWorkedTime
                        / totalAvailableTime) * 100;

        metrics.setTechnicianUtilization(
                Math.round(utilization * 100) / 100.0
        );
    }

    private void lunchInterruptionsCalculation(
            Metrics metrics,
            List<ScheduleEntry> schedule,
            List<Technician> technicians
    ) {

        int interruptions = 0;

        for (ScheduleEntry entry : schedule) {

            Technician technician = technicians.stream()
                    .filter(t ->
                            t.getId().equals(
                                    entry.getTechnicianId()
                            )
                    )
                    .findFirst()
                    .orElse(null);

            if (
                    technician == null
                            ||
                            technician.getLunchBreak() == null
            ) {
                continue;
            }

            TimeSlot analysisSlot =
                    new TimeSlot(
                            entry.getStartTime(),
                            entry.getEndTime()
                    );

            if (
                    technician.getLunchBreak()
                            .overlaps(analysisSlot)
            ) {
                interruptions++;
            }
        }

        metrics.setLunchInterruptions(interruptions);
    }
}
