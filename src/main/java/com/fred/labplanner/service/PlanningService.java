package com.fred.labplanner.service;


import com.fred.labplanner.model.*;
import com.fred.labplanner.model._enum.ETechnicianSpeciality;
import com.fred.labplanner.model.payload.PlanningRequest;
import com.fred.labplanner.model.payload.PlanningResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
public class PlanningService {
  public PlanningResponse planify(PlanningRequest request) {

    // validate request data consistency and identifier uniqueness
    validateRequestIntegrity(request);

    List<Sample> sortedSampleList = sortSamples(request.getSamples());

    List<ScheduleEntry> schedule = buildSchedule(
      sortedSampleList,
      request.getTechnicians(),
      request.getEquipment()
    );

    Metrics metrics = calculateMetrics(schedule);

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
   *
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
   * @param elements collection to validate
   * @param idExtractor function used to extract the identifier
   * from each element
   * @param entityName entity type name used in error messages
   *
   * @param <T> element type
   *
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
   * @param samples sorted sample list
   * @param technicians available laboratory technicians
   * @param equipmentList available laboratory equipment
   *
   * @return generated laboratory schedule
   */
  private List<ScheduleEntry> buildSchedule(
    List<Sample> samples,
    List<Technician> technicians,
    List<Equipment> equipmentList
  ) {

    List<ScheduleEntry> schedule = new ArrayList<>();


    // Track next availability for each resource
    Map<String, LocalTime> technicianAvailability = new HashMap<>();
    Map<String, LocalTime> equipmentAvailability = new HashMap<>();

    // Initialize technicians availability
    for (Technician technician : technicians) {
      technicianAvailability.put(
        technician.getId(),
        technician.getStartTime()
      );
    }

    // Initialize equipment availability
    for (Equipment equipment : equipmentList) {
      equipmentAvailability.put(
        equipment.getId(),
        LocalTime.MIN
      );
    }

    // Process samples by priority order
    for (Sample sample : samples) {

      Technician technician = findCompatibleTechnician(
        sample,
        technicians,
        technicianAvailability
      );

      Equipment equipment = findCompatibleEquipment(
        sample,
        equipmentList,
        equipmentAvailability
      );

      // Calculate next available slot
      LocalTime startTime = Collections.max(List.of(
        sample.getArrivalTime(),
        technicianAvailability.get(technician.getId()),
        equipmentAvailability.get(equipment.getId())
      ));

      LocalTime endTime = startTime.plusMinutes(
        sample.getAnalysisTime()
      );

      // Block resources
      technicianAvailability.put(
        technician.getId(),
        endTime
      );

      equipmentAvailability.put(
        equipment.getId(),
        endTime
      );

      // Add planning entry
      schedule.add(
        new ScheduleEntry(
          sample.getId(),
          technician.getId(),
          equipment.getId(),
          startTime,
          endTime,
          sample.getPriority()
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
   * @param sample       sample to process
   * @param technicians  available laboratory technicians
   * @param availability next availability time for each technician
   * @return the most suitable available technician
   * @throws RuntimeException if no compatible technician exists
   * @throws RuntimeException if no compatible technician is available
   *                          during working hours
   */
  private Technician findCompatibleTechnician(
    Sample sample,
    List<Technician> technicians,
    Map<String, LocalTime> availability
  ) {

    // Filter technicians compatible with sample type
    List<Technician> compatibleTechnicians = technicians.stream()

      .filter(technician ->
        technician.getSpeciality().name().equals(sample.getType().name())
          ||
          technician.getSpeciality()
            == ETechnicianSpeciality.GENERAL
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

      .filter(technician ->
        !availability.get(technician.getId())
          .plusMinutes(sample.getAnalysisTime())
          .isAfter(technician.getEndTime())
      )
      .min(
        Comparator.comparing(
          technician -> availability.get(technician.getId())
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
   * @param sample sample to process
   * @param equipmentList available laboratory equipment
   * @param availability next availability time for each equipment
   *
   * @return the most suitable available equipment
   *
   * @throws RuntimeException if no compatible equipment is found
   */
  private Equipment findCompatibleEquipment(
    Sample sample,
    List<Equipment> equipmentList,
    Map<String, LocalTime> availability
  ) {

    return equipmentList.stream()

      .filter(Equipment::isAvailable)
      .filter(equipment ->
        equipment.getType().name()
          .equals(sample.getType().name())
      )
      .min(
        Comparator.comparing(
          equipment -> availability.get(equipment.getId())
        )
      )
      .orElseThrow(() ->
        new RuntimeException(
          "No compatible equipment found"
        )
      );
  }

  // ---------- Metrics  ---------- //

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
   *
   * @return calculated planning metrics
   */
  private Metrics calculateMetrics(List<ScheduleEntry> schedule) {

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
   *
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
