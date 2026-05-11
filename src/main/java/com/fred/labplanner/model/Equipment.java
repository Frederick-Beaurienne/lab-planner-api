package com.fred.labplanner.model;

import com.fred.labplanner.model._enum.EEquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Equipment {

  @NotBlank
  private String id;

  private String name;

  @NotNull
  private EEquipmentType type;

  private boolean available;

  public Equipment() {
  }

  public Equipment(String id, String name, EEquipmentType type, boolean available) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.available = available;
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

  public EEquipmentType getType() {
    return type;
  }

  public void setType(EEquipmentType type) {
    this.type = type;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Equipment{");
    sb.append("id='").append(id).append('\'');
    sb.append(", name='").append(name).append('\'');
    sb.append(", type=").append(type);
    sb.append(", available=").append(available);
    sb.append('}');
    return sb.toString();
  }
}
