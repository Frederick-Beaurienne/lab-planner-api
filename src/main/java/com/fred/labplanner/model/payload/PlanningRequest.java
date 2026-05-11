package com.fred.labplanner.model.payload;

import com.fred.labplanner.model.Equipment;
import com.fred.labplanner.model.Sample;
import com.fred.labplanner.model.Technician;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class PlanningRequest {

  @NotEmpty
  private List<@Valid Sample> samples = new ArrayList<>();

  @NotEmpty
  private List<@Valid Technician> technicians = new ArrayList<>();

  @NotEmpty
  private List<@Valid Equipment> equipment = new ArrayList<>();

  public PlanningRequest() {
  }

  public PlanningRequest(List<Sample> samples, List<Technician> technicians, List<Equipment> equipment) {
    this.samples = samples;
    this.technicians = technicians;
    this.equipment = equipment;
  }

  public List<Sample> getSamples() {
    return samples;
  }

  public void setSamples(List<Sample> samples) {
    this.samples = samples;
  }

  public List<Technician> getTechnicians() {
    return technicians;
  }

  public void setTechnicians(List<Technician> technicians) {
    this.technicians = technicians;
  }

  public List<Equipment> getEquipment() {
    return equipment;
  }

  public void setEquipment(List<Equipment> equipment) {
    this.equipment = equipment;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PlanningRequest{");
    sb.append("samples=").append(samples);
    sb.append(", technicians=").append(technicians);
    sb.append(", equipment=").append(equipment);
    sb.append('}');
    return sb.toString();
  }
}
