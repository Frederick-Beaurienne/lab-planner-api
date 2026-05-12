package com.fred.labplanner.model.payload.metadata;

import java.util.ArrayList;
import java.util.List;

public class PlanningMetadata {

    private List<LunchBreakInfo> lunchBreaks = new ArrayList<>();

    private List<EquipmentCleaningInfo> equipmentCleaning = new ArrayList<>();

    // ---------- CONSTRUCTORS ---------- //

    public PlanningMetadata() {
    }

    public PlanningMetadata(List<LunchBreakInfo> lunchBreaks, List<EquipmentCleaningInfo> equipmentCleaning) {
        this.lunchBreaks = lunchBreaks;
        this.equipmentCleaning = equipmentCleaning;
    }

    // ---------- GETTERS AND SETTERS ---------- //

    public List<LunchBreakInfo> getLunchBreaks() {
        return lunchBreaks;
    }

    public void setLunchBreaks(List<LunchBreakInfo> lunchBreaks) {
        this.lunchBreaks = lunchBreaks;
    }

    public List<EquipmentCleaningInfo> getEquipmentCleaning() {
        return equipmentCleaning;
    }

    public void setEquipmentCleaning(List<EquipmentCleaningInfo> equipmentCleaning) {
        this.equipmentCleaning = equipmentCleaning;
    }

    // ---------- TO STRING ---------- //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlanningMetadata{");
        sb.append("lunchBreaks=").append(lunchBreaks);
        sb.append(", equipmentCleaning=").append(equipmentCleaning);
        sb.append('}');
        return sb.toString();
    }
}
