package com.fred.labplanner.model;

import com.fred.labplanner.model._enum.EAnalysisCategory;
import com.fred.labplanner.model._enum.EEquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Equipment {

    @NotBlank
    private String id;

    private String name;

    @NotNull
    private EEquipmentType type;

    private int capacity = 1;

    private TimeSlot maintenanceWindow; // TODO manage

    /**
     * in minutes
     */
    private long cleaningTime = 0;

    private boolean available = true;

    // ---------- CACHE ---------- //

    private EAnalysisCategory analysisCategory;

    // ---------- CONSTRUCTORS ---------- //

    public Equipment() {
    }

    public Equipment(String id, String name, EEquipmentType type, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.available = available;
    }

    public Equipment(String id, String name, EEquipmentType type, int capacity, TimeSlot maintenanceWindow, long cleaningTime, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.maintenanceWindow = maintenanceWindow;
        this.cleaningTime = cleaningTime;
        this.available = available;
    }

    // ---------- GETTERS AND SETTERS ---------- //

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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public TimeSlot getMaintenanceWindow() {
        return maintenanceWindow;
    }

    public void setMaintenanceWindow(TimeSlot maintenanceWindow) {
        this.maintenanceWindow = maintenanceWindow;
    }

    public long getCleaningTime() {
        return cleaningTime;
    }

    public void setCleaningTime(long cleaningTime) {
        this.cleaningTime = cleaningTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // ---------- TO STRING ---------- //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Equipment{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", capacity=").append(capacity);
        sb.append(", maintenanceWindow=").append(maintenanceWindow);
        sb.append(", cleaningTime=").append(cleaningTime);
        sb.append(", available=").append(available);
        sb.append('}');
        return sb.toString();
    }

    // ---------- CLASS METHODE ---------- //

    public EAnalysisCategory resolveAnalysisCategory() {

        if (analysisCategory != null) {
            return analysisCategory;
        }

        return switch (type) {

            // ===== V1 compatibility =====

            case BLOOD -> EAnalysisCategory.BLOOD;

            case URINE -> EAnalysisCategory.MICROBIOLOGY;

            case TISSUE -> EAnalysisCategory.MICROBIOLOGY;

            // ===== V2 direct mapping =====

            case CHEMISTRY -> EAnalysisCategory.CHEMISTRY;

            case MICROBIOLOGY -> EAnalysisCategory.MICROBIOLOGY;

            case IMMUNOLOGY -> EAnalysisCategory.IMMUNOLOGY;

            case GENETICS -> EAnalysisCategory.GENETICS;
        };
    }
}
