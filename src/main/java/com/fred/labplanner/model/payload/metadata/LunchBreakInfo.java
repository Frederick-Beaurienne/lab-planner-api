package com.fred.labplanner.model.payload.metadata;

import com.fred.labplanner.model.TimeSlot;

public class LunchBreakInfo {

    private String technicianId;

    private TimeSlot planned;

    private TimeSlot actual;

    private String reason;

    // ---------- CONSTRUCTORS ---------- //

    public LunchBreakInfo() {
    }

    public LunchBreakInfo(String technicianId, TimeSlot planned, TimeSlot actual, String reason) {
        this.technicianId = technicianId;
        this.planned = planned;
        this.actual = actual;
        this.reason = reason;
    }

    // ---------- GETTERS AND SETTERS ---------- //

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public TimeSlot getPlanned() {
        return planned;
    }

    public void setPlanned(TimeSlot planned) {
        this.planned = planned;
    }

    public TimeSlot getActual() {
        return actual;
    }

    public void setActual(TimeSlot actual) {
        this.actual = actual;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // ---------- TO STRING ---------- //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LunchBreakInfo{");
        sb.append("technicianId='").append(technicianId).append('\'');
        sb.append(", planned=").append(planned);
        sb.append(", actual=").append(actual);
        sb.append(", reason='").append(reason).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
