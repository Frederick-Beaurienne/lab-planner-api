package com.fred.labplanner.model;

public class Metrics {
  private long totalTime;
  private double efficiency;
  private int conflicts;

  public Metrics() {
  }

  public Metrics(long totalTime, double efficiency, int conflicts) {
    this.totalTime = totalTime;
    this.efficiency = efficiency;
    this.conflicts = conflicts;
  }

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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Metrics{");
    sb.append("totalTime=").append(totalTime);
    sb.append(", efficiency=").append(efficiency);
    sb.append(", conflicts=").append(conflicts);
    sb.append('}');
    return sb.toString();
  }
}
