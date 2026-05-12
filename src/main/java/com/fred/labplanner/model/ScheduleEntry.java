package com.fred.labplanner.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fred.labplanner.model._enum.EPriority;

import java.time.LocalTime;

@JsonPropertyOrder({
        "id",
        "technicianId",
        "equipmentId",
        "startTime",
        "endTime",
        "duration",
        "priority",
        "efficiency"
})
public class ScheduleEntry {
    private String id;
    private EPriority priority;
    private String technicianId;
    private String equipmentId;
    private LocalTime startTime;
    private LocalTime endTime;
    /**
     * in minutes
     */
    private Long duration; // TODO manage
    private Double efficiency; // TODO manage

    // ---------- CONSTRUCTORS ---------- //

    public ScheduleEntry() {
    }

    public ScheduleEntry(String id, String technicianId, String equipmentId, LocalTime startTime, LocalTime endTime, EPriority priority) {
        this.id = id;
        this.technicianId = technicianId;
        this.equipmentId = equipmentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
    }

    public ScheduleEntry(String id, String technicianId, String equipmentId, LocalTime startTime, LocalTime endTime, EPriority priority, Long duration, Double efficiency) {
        this.id = id;
        this.technicianId = technicianId;
        this.equipmentId = equipmentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.duration = duration;
        this.efficiency = efficiency;
    }

    // ---------- GETTERS AND SETTERS ---------- //

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EPriority getPriority() {
        return priority;
    }

    public void setPriority(EPriority priority) {
        this.priority = priority;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Double efficiency) {
        this.efficiency = efficiency;
    }


    // ---------- TO STRING ---------- //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScheduleEntry{");
        sb.append("id='").append(id).append('\'');
        sb.append(", priority=").append(priority);
        sb.append(", technicianId='").append(technicianId).append('\'');
        sb.append(", equipmentId='").append(equipmentId).append('\'');
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", duration=").append(duration);
        sb.append(", efficiency=").append(efficiency);
        sb.append('}');
        return sb.toString();
    }
}
