package com.fred.labplanner.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "totalTime",
        "efficiency",
        "averageWaitTime",
        "conflicts",
        "priorityRespectRate",
        "parallelismRate",
        "technicianUtilization",
        "statResponseTime",
        "constraintViolations",
        "lunchInterruptions"
})
public class Metrics {
    private long totalTime;
    private double efficiency;
    private int conflicts;

    /**
     * in minutes
     */
    private Long statResponseTime; // TODO manage
    private Integer lunchInterruptions; // TODO manage
    private Integer constraintViolations; // TODO manage

    /**
     * in percent
     */
    private Double technicianUtilization; // TODO manage

    /**
     * in percent
     */
    private Double priorityRespectRate; // TODO manage

    /**
     * in minutes
     */
    private Long averageWaitTime; // TODO manage

    /**
     * in percent
     */
    private Double parallelismRate; // TODO manage

    // ---------- CONSTRUCTORS ---------- //

    public Metrics() {
    }

    public Metrics(long totalTime, double efficiency, int conflicts) {
        this.totalTime = totalTime;
        this.efficiency = efficiency;
        this.conflicts = conflicts;
    }

    public Metrics(long totalTime, double efficiency, int conflicts, Long statResponseTime, Integer lunchInterruptions, Integer constraintViolations, Double technicianUtilization, Double priorityRespectRate, Long averageWaitTime, Double parallelismRate) {
        this.totalTime = totalTime;
        this.efficiency = efficiency;
        this.conflicts = conflicts;
        this.statResponseTime = statResponseTime;
        this.lunchInterruptions = lunchInterruptions;
        this.constraintViolations = constraintViolations;
        this.technicianUtilization = technicianUtilization;
        this.priorityRespectRate = priorityRespectRate;
        this.averageWaitTime = averageWaitTime;
        this.parallelismRate = parallelismRate;
    }

    // ---------- GETTERS AND SETTERS ---------- //

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public int getConflicts() {
        return conflicts;
    }

    public void setConflicts(int conflicts) {
        this.conflicts = conflicts;
    }

    public Long getStatResponseTime() {
        return statResponseTime;
    }

    public void setStatResponseTime(Long statResponseTime) {
        this.statResponseTime = statResponseTime;
    }

    public Integer getLunchInterruptions() {
        return lunchInterruptions;
    }

    public void setLunchInterruptions(Integer lunchInterruptions) {
        this.lunchInterruptions = lunchInterruptions;
    }

    public Integer getConstraintViolations() {
        return constraintViolations;
    }

    public void setConstraintViolations(Integer constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    public Double getTechnicianUtilization() {
        return technicianUtilization;
    }

    public void setTechnicianUtilization(Double technicianUtilization) {
        this.technicianUtilization = technicianUtilization;
    }

    public Double getPriorityRespectRate() {
        return priorityRespectRate;
    }

    public void setPriorityRespectRate(Double priorityRespectRate) {
        this.priorityRespectRate = priorityRespectRate;
    }

    public Long getAverageWaitTime() {
        return averageWaitTime;
    }

    public void setAverageWaitTime(Long averageWaitTime) {
        this.averageWaitTime = averageWaitTime;
    }

    public Double getParallelismRate() {
        return parallelismRate;
    }

    public void setParallelismRate(Double parallelismRate) {
        this.parallelismRate = parallelismRate;
    }


    // ---------- TO STRING ---------- //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Metrics{");
        sb.append("totalTime=").append(totalTime);
        sb.append(", efficiency=").append(efficiency);
        sb.append(", conflicts=").append(conflicts);
        sb.append(", statResponseTime=").append(statResponseTime);
        sb.append(", lunchInterruptions=").append(lunchInterruptions);
        sb.append(", constraintViolations=").append(constraintViolations);
        sb.append(", technicianUtilization=").append(technicianUtilization);
        sb.append(", priorityRespectRate=").append(priorityRespectRate);
        sb.append(", averageWaitTime=").append(averageWaitTime);
        sb.append(", parallelismRate=").append(parallelismRate);
        sb.append('}');
        return sb.toString();
    }
}
