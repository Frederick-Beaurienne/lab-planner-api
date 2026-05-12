package com.fred.labplanner.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fred.labplanner.config.LaboratoryDefaults;
import com.fred.labplanner.model._enum.EAnalysisCategory;
import com.fred.labplanner.model._enum.ETechnicianSpeciality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Technician {

    @NotBlank
    private String id;

    private String name;

    @JsonAlias("speciality")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    @NotEmpty
    private Set<ETechnicianSpeciality> specialty = new HashSet<>(); // TODO manage list

    private Double efficiency = 1.0; // TODO manage

    @NotNull
    private LocalTime startTime = LaboratoryDefaults.DEFAULT_TECHNICIAN_START_TIME;

    @NotNull
    private LocalTime endTime = LaboratoryDefaults.DEFAULT_TECHNICIAN_END_TIME;

    /**
     * Technician lunch break time slot.
     *
     * <p>
     * Default laboratory lunch break is automatically applied when the
     * field is not provided in the incoming request.
     * </p>
     *
     * <p>
     * JSON behavior:
     * </p>
     *
     * <ul>
     *   <li>Missing field → default lunch break applied</li>
     *   <li>{@code null} value → no lunch break</li>
     *   <li>Valid time slot → custom lunch break applied</li>
     * </ul>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TimeSlot lunchBreak = LaboratoryDefaults.DEFAULT_LUNCH_BREAK; // TODO manage

    // ---------- CACHE ---------- //

    private Set<EAnalysisCategory> resolvedAnalysisCategories;

    // ---------- CONSTRUCTORS ---------- //

    @JsonCreator(mode = JsonCreator.Mode.DEFAULT)
    public Technician() {
    }

    public Technician(String id, String name, Set<ETechnicianSpeciality> specialty, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Technician(String id, String name, Set<ETechnicianSpeciality> specialty, Double efficiency, LocalTime startTime, LocalTime endTime, TimeSlot lunchBreak) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.efficiency = efficiency;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lunchBreak = lunchBreak;
    }

// ---------- GETTERS AND SETTERS ---------- //

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ETechnicianSpeciality> getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Set<ETechnicianSpeciality> specialty) {
        this.specialty = specialty;

        // invalidate cache
        this.resolvedAnalysisCategories = null;
    }

    public Double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Double efficiency) {
        this.efficiency = efficiency;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public TimeSlot getLunchBreak() {
        return lunchBreak;
    }

    public void setLunchBreak(TimeSlot lunchBreak) {
        this.lunchBreak = lunchBreak;
    }

// ---------- TO STRING ---------- //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Technician{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", speciality=").append(specialty);
        sb.append(", efficiency=").append(efficiency);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", lunchBreak=").append(lunchBreak);
        sb.append('}');
        return sb.toString();
    }

    // ---------- CLASS METHODE ---------- //

    public Set<EAnalysisCategory> resolveAnalysisCategories() {

        if (resolvedAnalysisCategories != null) {
            return resolvedAnalysisCategories;
        }

        if (specialty == null || specialty.isEmpty()) {
            resolvedAnalysisCategories = Set.of();
            return resolvedAnalysisCategories;
        }

        resolvedAnalysisCategories = specialty.stream()
                .map(speciality -> switch (speciality) {

                    // ===== V1 compatibility =====
                    case BLOOD -> EAnalysisCategory.BLOOD;
                    case URINE -> EAnalysisCategory.MICROBIOLOGY;
                    case TISSUE -> EAnalysisCategory.MICROBIOLOGY;

                    // ===== V2 direct mapping =====
                    case CHEMISTRY -> EAnalysisCategory.CHEMISTRY;
                    case MICROBIOLOGY -> EAnalysisCategory.MICROBIOLOGY;
                    case IMMUNOLOGY -> EAnalysisCategory.IMMUNOLOGY;
                    case GENETICS -> EAnalysisCategory.GENETICS;

                    // GENERAL does not map to a specific analysis category
                    case GENERAL -> null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return resolvedAnalysisCategories;
    }

}
