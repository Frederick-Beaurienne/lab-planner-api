package com.fred.labplanner.service.planning;


import com.fred.labplanner.model.*;
import com.fred.labplanner.model._enum.ETechnicianSpeciality;
import com.fred.labplanner.model.payload.PlanningRequest;
import com.fred.labplanner.model.payload.PlanningResponse;
import com.fred.labplanner.service.planning.metrics.MetricsCalculator;
import com.fred.labplanner.service.planning.scheduling.ResourceTimeline;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;

/**
 * Service responsible for laboratory analysis planning generation.
 *
 * <p>
 * This service processes laboratory samples and generates an optimized
 * schedule based on:
 * </p>
 *
 * <ul>
 *   <li>Sample priority</li>
 *   <li>Technician compatibility and availability</li>
 *   <li>Equipment compatibility and availability</li>
 *   <li>Working hour constraints</li>
 * </ul>
 *
 * <p>
 * The generated planning also includes scheduling metrics such as:
 * </p>
 *
 * <ul>
 *   <li>Total planning duration</li>
 *   <li>Resource efficiency</li>
 *   <li>Detected scheduling conflicts</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PlanningService {

    private final MetricsCalculator metricsCalculator;

    public PlanningResponse planify(PlanningRequest request) {

        // validate request data consistency and identifier uniqueness
        validateRequestIntegrity(request);

        List<Sample> sortedSampleList = sortSamples(request.getSamples());

        List<ScheduleEntry> schedule = buildSchedule(
                sortedSampleList,
                request.getTechnicians(),
                request.getEquipment()
        );

        Metrics metrics = metricsCalculator.calculateMetrics(schedule, request.getTechnicians(), request.getSamples());

        return new PlanningResponse(schedule, metrics);
    }

    // ---------- Helper methods ---------- //

    /**
     * Validates the integrity and consistency of the planning request data.
     *
     * <p>
     * Validation includes:
     * </p>
     *
     * <ul>
     *   <li>Unique sample identifiers</li>
     *   <li>Unique technician identifiers</li>
     *   <li>Unique equipment identifiers</li>
     * </ul>
     *
     * <p>
     * The validation is performed before schedule generation to prevent
     * inconsistent or invalid planning data.
     * </p>
     *
     * @param request planning request to validate
     * @throws RuntimeException if duplicate identifiers are detected
     */
    private void validateRequestIntegrity(
            PlanningRequest request
    ) {

        validateUniqueIds(
                request.getSamples(),
                Sample::getId,
                "sample"
        );

        validateUniqueIds(
                request.getTechnicians(),
                Technician::getId,
                "technician"
        );

        validateUniqueIds(
                request.getEquipment(),
                Equipment::getId,
                "equipment"
        );
    }

    /**
     * Validates identifier uniqueness within a collection.
     *
     * <p>
     * The method checks that no duplicate identifiers exist
     * for the provided entity type.
     * </p>
     *
     * @param elements    collection to validate
     * @param idExtractor function used to extract the identifier
     *                    from each element
     * @param entityName  entity type name used in error messages
     * @param <T>         element type
     * @throws RuntimeException if duplicate identifiers are detected
     */
    private <T> void validateUniqueIds(
            List<T> elements,
            Function<T, String> idExtractor,
            String entityName
    ) {

        Set<String> ids = new HashSet<>();

        for (T element : elements) {

            String id = idExtractor.apply(element);

            if (!ids.add(id)) {

                throw new RuntimeException(
                        "Duplicate " + entityName + " id: " + id
                );
            }
        }
    }

    /**
     * Returns samples sorted by scheduling priority and arrival time.
     *
     * <p>
     * Sorting rules:
     * </p>
     *
     * <ul>
     *   <li>STAT samples first</li>
     *   <li>Then URGENT samples</li>
     *   <li>Then ROUTINE samples</li>
     *   <li>Arrival time used as secondary sort criteria</li>
     * </ul>
     *
     * @param samples samples to sort
     * @return sorted sample list
     */
    private List<Sample> sortSamples(List<Sample> samples) {

        // preserve the original request sample list
        List<Sample> sortedSamples = new ArrayList<>(samples);

        // sort samples by priority first, then by arrival time
        sortedSamples.sort(
                Comparator
                        .comparingInt((Sample s) -> s.getPriority().getLevel())
                        .thenComparing(Sample::getArrivalTime)
        );

        return sortedSamples;
    }

    /**
     * Builds the laboratory analysis schedule.
     *
     * <p>
     * Samples are processed in priority order and assigned to compatible
     * technicians and equipment according to resource availability.
     * </p>
     *
     * <p>
     * For each sample, the algorithm:
     * </p>
     *
     * <ol>
     *   <li>Finds a compatible technician</li>
     *   <li>Finds compatible equipment</li>
     *   <li>Calculates the next available time slot</li>
     *   <li>Blocks the selected resources during analysis time</li>
     *   <li>Adds the generated entry to the schedule</li>
     * </ol>
     *
     * <p>
     * Resource availability is updated after each assignment to prevent
     * scheduling conflicts.
     * </p>
     *
     * @param samples       sorted sample list
     * @param technicians   available laboratory technicians
     * @param equipmentList available laboratory equipment
     * @return generated laboratory schedule
     */
    private List<ScheduleEntry> buildSchedule(
            List<Sample> samples,
            List<Technician> technicians,
            List<Equipment> equipmentList
    ) {

        List<ScheduleEntry> schedule = new ArrayList<>();

        // Track next availability for each resource
        Map<String, ResourceTimeline> technicianTimelines = new HashMap<>();
        Map<String, ResourceTimeline> equipmentTimelines = new HashMap<>();

        // Initialize technicians availability
        for (Technician technician : technicians) {
            ResourceTimeline timeline = new ResourceTimeline();

            /// Block time before technician working hours
            timeline.reserve(
                    LocalTime.MIN,
                    technician.getStartTime()
            );
            technicianTimelines.put(
                    technician.getId(),
                    timeline
            );

            // Block lunch break
            if (technician.getLunchBreak() != null) {

                timeline.reserve(
                        technician.getLunchBreak().getStart(),
                        technician.getLunchBreak().getEnd()
                );
            }
        }

        // Initialize equipment availability
        for (Equipment equipment : equipmentList) {
            equipmentTimelines.put(
                    equipment.getId(),
                    new ResourceTimeline()
            );
        }

        // Process samples by priority order
        for (Sample sample : samples) {

            Technician technician = findCompatibleTechnician(
                    sample,
                    technicians,
                    technicianTimelines
            );

            Equipment equipment = findCompatibleEquipment(
                    sample,
                    equipmentList,
                    equipmentTimelines,
                    technician
            );

            // real time calcul
            long effectiveDuration = calculateEffectiveDuration(sample, technician);

            // Calculate next available slot
            LocalTime startTime = Collections.max(List.of(
                    sample.getArrivalTime(),
                    technicianTimelines.get(technician.getId())
                            .getNextAvailableTime(sample.getArrivalTime(), effectiveDuration),
                    equipmentTimelines.get(equipment.getId())
                            .getNextAvailableTime(sample.getArrivalTime(), effectiveDuration)
            ));


            LocalTime endTime = startTime.plusMinutes(effectiveDuration);

            // Block resources including cleaning time
            technicianTimelines.get(technician.getId())
                    .reserve(startTime, endTime);

            equipmentTimelines.get(equipment.getId())
                    .reserve(startTime,
                            endTime.plusMinutes(equipment.getCleaningTime()));

            // Add planning entry
            schedule.add(
                    new ScheduleEntry(
                            sample.getId(),
                            technician.getId(),
                            equipment.getId(),
                            startTime,
                            endTime,
                            sample.getPriority(),
                            effectiveDuration,
                            technician.getEfficiency()
                    )
            );
        }

        return schedule;
    }

    /**
     * Finds the most suitable technician for a sample analysis.
     *
     * <p>
     * Selection process:
     * </p>
     *
     * <ol>
     *   <li>Filter technicians compatible with the sample type</li>
     *   <li>Keep only technicians available within working hours</li>
     *   <li>Select the technician available the earliest</li>
     * </ol>
     *
     * <p>
     * Technicians with the {@code GENERAL} speciality are considered
     * compatible with all sample types.
     * </p>
     *
     * <p>
     * When multiple technicians are available at the same time,
     * specialized technicians are prioritized over {@code GENERAL}
     * technicians.
     * </p>
     *
     * @param sample      sample to process
     * @param technicians available laboratory technicians
     * @param timelines   technicians timelines associated with each equipment
     * @return the most suitable available technician
     * @throws RuntimeException if no compatible technician exists
     * @throws RuntimeException if no compatible technician is available
     *                          during working hours
     */
    private Technician findCompatibleTechnician(
            Sample sample,
            List<Technician> technicians,
            Map<String, ResourceTimeline> timelines
    ) {

        // Filter technicians compatible with sample type
        List<Technician> compatibleTechnicians = technicians.stream()

                .filter(technician ->
                        technician.resolveAnalysisCategories().contains(
                                sample.resolveAnalysisCategory())
                                ||
                                technician.getSpecialty().contains(
                                        ETechnicianSpeciality.GENERAL)
                )
                .toList();

        // if no compatible technician found
        if (compatibleTechnicians.isEmpty()) {
            throw new RuntimeException(
                    "No compatible technician found"
            );
        }

        // Keep only technicians available during working hours
        return compatibleTechnicians.stream()

                .filter(technician -> !timelines
                        .get(technician.getId())
                        .getNextAvailableTime(sample.getArrivalTime(), calculateEffectiveDuration(sample, technician))
                        .plusMinutes(calculateEffectiveDuration(sample, technician))
                        .isAfter(technician.getEndTime())
                )
                .min(
                        Comparator.comparing(
                                technician -> timelines
                                        .get(technician.getId())
                                        .getNextAvailableTime(sample.getArrivalTime(), calculateEffectiveDuration(sample, technician))
                        )
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "No technician available within working hours"
                        )
                );
    }

    /**
     * Finds the most suitable equipment for a sample analysis.
     *
     * <p>
     * Selection process:
     * </p>
     *
     * <ol>
     *   <li>Filter available equipment compatible with the sample type</li>
     *   <li>Select the equipment available the earliest</li>
     * </ol>
     *
     * <p>
     * Only equipment matching the sample type can be selected.
     * </p>
     *
     * @param sample        sample to process
     * @param equipmentList available laboratory equipment
     * @param timelines     resource timelines associated with each equipment
     * @return the most suitable available equipment
     * @throws RuntimeException if no compatible equipment is found
     */
    private Equipment findCompatibleEquipment(
            Sample sample,
            List<Equipment> equipmentList,
            Map<String, ResourceTimeline> timelines,
            Technician technician
    ) {

        return equipmentList.stream()

                .filter(Equipment::isAvailable)
                .filter(equipment -> equipment
                        .resolveAnalysisCategory() == sample.resolveAnalysisCategory()
                )
                .min(
                        Comparator.comparing(equipment -> timelines
                                .get(equipment.getId())
                                .getNextAvailableTime(sample.getArrivalTime(), calculateEffectiveDuration(sample, technician))
                        )
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "No compatible equipment found"
                        )
                );
    }

    private long calculateEffectiveDuration(
            Sample sample,
            Technician technician
    ) {
        return Math.round(
                sample.getAnalysisTime()
                        / technician.getEfficiency()
        );
    }
}
