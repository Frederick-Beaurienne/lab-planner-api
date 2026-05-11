package com.fred.labplanner.model.payload;

import com.fred.labplanner.model.Metrics;
import com.fred.labplanner.model.ScheduleEntry;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class PlanningResponse {

  private List<ScheduleEntry> schedule = new ArrayList<>();
  private Metrics metrics;

  public PlanningResponse() {
  }

  public PlanningResponse(List<ScheduleEntry> schedule, Metrics metrics) {
    this.schedule = schedule;
    this.metrics = metrics;
  }

  public List<ScheduleEntry> getSchedule() {
    return schedule;
  }

  public void setSchedule(List<ScheduleEntry> schedule) {
    this.schedule = schedule;
  }

  public Metrics getMetrics() {
    return metrics;
  }

  public void setMetrics(Metrics metrics) {
    this.metrics = metrics;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PlanningResponse{");
    sb.append("schedule=").append(schedule);
    sb.append(", metrics=").append(metrics);
    sb.append('}');
    return sb.toString();
  }
}
