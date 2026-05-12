package com.fred.labplanner.model.payload.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fred.labplanner.json.deserializer.TimeSlotDeserializer;
import com.fred.labplanner.model.TimeSlot;

public class EquipmentCleaningInfo {

    private String equipmentId;

    private TimeSlot between;

    private int duration;

    // ---------- CONSTRUCTORS ---------- //

    public EquipmentCleaningInfo() {
    }

    public EquipmentCleaningInfo(String equipmentId, TimeSlot between, int duration) {
        this.equipmentId = equipmentId;
        this.between = between;
        this.duration = duration;
    }

    // ---------- GETTERS AND SETTERS ---------- //

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public TimeSlot getBetween() {
        return between;
    }

    public void setBetween(TimeSlot between) {
        this.between = between;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    // ---------- TO STRING ---------- //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EquipmentCleaningInfo{");
        sb.append("equipmentId='").append(equipmentId).append('\'');
        sb.append(", between=").append(between);
        sb.append(", duration=").append(duration);
        sb.append('}');
        return sb.toString();
    }
}
