package com.fred.labplanner.model.payload;

import com.fred.labplanner.model.Metrics;
import com.fred.labplanner.model.ScheduleEntry;
import com.fred.labplanner.model.payload.metadata.PlanningMetadata;

import java.util.ArrayList;
import java.util.List;

public class PlanningResponse {

  private List<ScheduleEntry> schedule = new ArrayList<>();
  private PlanningMetadata metadata = new PlanningMetadata(); // TODO manage
  private Metrics metrics;

  // ---------- CONSTRUCTORS ---------- //

  public PlanningResponse() {
  }

  public PlanningResponse(List<ScheduleEntry> schedule, Metrics metrics) {
    this.schedule = schedule;
    this.metrics = metrics;
  }

  public PlanningResponse(List<ScheduleEntry> schedule, PlanningMetadata metadata, Metrics metrics) {
    this.schedule = schedule;
    this.metadata = metadata;
    this.metrics = metrics;
  }

  // ---------- GETTERS AND SETTERS ---------- //

  public List<ScheduleEntry> getSchedule() {
    return schedule;
  }

  public void setSchedule(List<ScheduleEntry> schedule) {
    this.schedule = schedule;
  }

  public PlanningMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(PlanningMetadata metadata) {
    this.metadata = metadata;
  }

  public Metrics getMetrics() {
    return metrics;
  }

  public void setMetrics(Metrics metrics) {
    this.metrics = metrics;
  }

  // ---------- TO STRING ---------- //

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PlanningResponse{");
    sb.append("schedule=").append(schedule);
    sb.append(", metadata=").append(metadata);
    sb.append(", metrics=").append(metrics);
    sb.append('}');
    return sb.toString();
  }
}
