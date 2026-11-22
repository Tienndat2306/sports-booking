package com.huit.sportsbooking.model.mongo;

import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection: maintenance (Mục 3.4.3).
 */
@Document(collection = "maintenance")
public class MaintenanceDocument {

    @Id
    private String id;              // 01JM0001
    private String facilityId;
    private String courtId;
    private String reason;          // CLEANING | REPAIR | LIGHT | INCIDENT
    private String description;
    private boolean emergency;
    private List<Window> windows;
    private String status;          // SCHEDULED | ACTIVE | DONE | CANCELED
    private List<AffectedBooking> affectedBookings;
    private String reportedBy;
    private Instant completedAt;
    private Instant createdAt;

    public static class Window {
        private String day;         // "2026-11-22"
        private String fromSlot;    // "08:00"
        private String toSlot;      // "10:00" (toSlot không tính)

        public Window() {}

        public Window(String day, String fromSlot, String toSlot) {
            this.day = day;
            this.fromSlot = fromSlot;
            this.toSlot = toSlot;
        }

        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }
        public String getFromSlot() { return fromSlot; }
        public void setFromSlot(String fromSlot) { this.fromSlot = fromSlot; }
        public String getToSlot() { return toSlot; }
        public void setToSlot(String toSlot) { this.toSlot = toSlot; }
    }

    public static class AffectedBooking {
        private String bookingId;
        private String resolution;  // RESCHEDULE_COURT | RESCHEDULE_TIME | CANCEL_FULL_REFUND
        private String newCourtId;
        private boolean notified;

        public String getBookingId() { return bookingId; }
        public void setBookingId(String bookingId) { this.bookingId = bookingId; }
        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
        public String getNewCourtId() { return newCourtId; }
        public void setNewCourtId(String newCourtId) { this.newCourtId = newCourtId; }
        public boolean isNotified() { return notified; }
        public void setNotified(boolean notified) { this.notified = notified; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isEmergency() { return emergency; }
    public void setEmergency(boolean emergency) { this.emergency = emergency; }
    public List<Window> getWindows() { return windows; }
    public void setWindows(List<Window> windows) { this.windows = windows; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<AffectedBooking> getAffectedBookings() { return affectedBookings; }
    public void setAffectedBookings(List<AffectedBooking> affectedBookings) { this.affectedBookings = affectedBookings; }
    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
