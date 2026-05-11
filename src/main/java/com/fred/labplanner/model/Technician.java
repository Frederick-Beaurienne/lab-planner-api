package com.fred.labplanner.model;

import com.fred.labplanner.model._enum.ETechnicianSpeciality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class Technician {

  @NotBlank
  private String id;

  private String name;

  @NotNull
  private ETechnicianSpeciality speciality;

  @NotNull
  private LocalTime startTime;

  @NotNull
  private LocalTime endTime;

  public Technician() {
  }

  public Technician(String id, String name, ETechnicianSpeciality speciality, LocalTime startTime, LocalTime endTime) {
    this.id = id;
    this.name = name;
    this.speciality = speciality;
    this.startTime = startTime;
    this.endTime = endTime;
  }

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

  public ETechnicianSpeciality getSpeciality() {
    return speciality;
  }

  public void setSpeciality(ETechnicianSpeciality speciality) {
    this.speciality = speciality;
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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Technician{");
    sb.append("id='").append(id).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", speciality=").append(speciality);
    sb.append(", startTime=").append(startTime);
    sb.append(", endTime=").append(endTime);
    sb.append('}');
    return sb.toString();
  }
}
