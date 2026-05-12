package com.fred.labplanner.model;

import com.fred.labplanner.model._enum.EAnalysisCategory;
import com.fred.labplanner.model._enum.EPriority;
import com.fred.labplanner.model._enum.ESampleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;

public class Sample {

    @NotBlank
    private String id;

    @NotNull
    private ESampleType type;

    private String analysisType; // TODO manage

    @NotNull
    private EPriority priority;

    @Positive
    private long analysisTime;

    @NotNull
    private LocalTime arrivalTime;

    /**
     * Patient identifier associated with the sample.
     *
     * <p>
     * Currently not used by the scheduling algorithm,
     * but kept for domain consistency and future extensions.
     * </p>
     */
    private String patientId;

    // ---------- CACHE ---------- //

    private EAnalysisCategory analysisCategory;

    // ---------- CONSTRUCTORS ---------- //

    public Sample() {
    }

    public Sample(String id, ESampleType type, EPriority priority, long analysisTime, LocalTime arrivalTime, String patientId) {
        this.id = id;
        this.type = type;
        this.priority = priority;
        this.analysisTime = analysisTime;
        this.arrivalTime = arrivalTime;
        this.patientId = patientId;
    }

    public Sample(String id, ESampleType type, String analysisType, EPriority priority, long analysisTime, LocalTime arrivalTime, String patientId) {
        this.id = id;
        this.type = type;
        this.analysisType = analysisType;
        this.priority = priority;
        this.analysisTime = analysisTime;
        this.arrivalTime = arrivalTime;
        this.patientId = patientId;
    }

    // ---------- GETTERS AND SETTERS ---------- //

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ESampleType getType() {
        return type;
    }

    public void setType(ESampleType type) {
        this.type = type;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public EPriority getPriority() {
        return priority;
    }

    public void setPriority(EPriority priority) {
        this.priority = priority;
    }

    public long getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(long analysisTime) {
        this.analysisTime = analysisTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    // ---------- TO STRING ---------- //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Sample{");
        sb.append("id='").append(id).append('\'');
        sb.append(", type=").append(type);
        sb.append(", analysisType='").append(analysisType).append('\'');
        sb.append(", priority=").append(priority);
        sb.append(", analysisTime=").append(analysisTime);
        sb.append(", arrivalTime=").append(arrivalTime);
        sb.append(", patientId='").append(patientId).append('\'');
        sb.append('}');
        return sb.toString();
    }

    // ---------- CLASS METHODE ---------- //

    public EAnalysisCategory resolveAnalysisCategory() {

        if (analysisCategory != null) {
            return analysisCategory;
        }

        return switch (type) {
            case BLOOD -> EAnalysisCategory.BLOOD;
            case URINE -> EAnalysisCategory.MICROBIOLOGY;
            case TISSUE -> EAnalysisCategory.MICROBIOLOGY;
        };
    }
}
