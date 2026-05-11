package com.fred.labplanner.model;

import com.fred.labplanner.model._enum.EPriority;

import java.time.LocalTime;

public class ScheduleEntry {
  private String id;
  private String technicianId;
  private String equipmentId;
  private LocalTime startTime;
  private LocalTime endTime;
  private EPriority priority;

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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public EPriority getPriority() {
    return priority;
  }

  public void setPriority(EPriority priority) {
    this.priority = priority;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ScheduleEntry{");
    sb.append("id='").append(id).append('\'');
    sb.append(", technicianId='").append(technicianId).append('\'');
    sb.append(", equipmentId='").append(equipmentId).append('\'');
    sb.append(", startTime=").append(startTime);
    sb.append(", endTime=").append(endTime);
    sb.append(", priority=").append(priority);
    sb.append('}');
    return sb.toString();
  }
}
