package com.huit.sportsbooking.model.mongo;

import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection: bookings (Mục 3.4.3).
 */
@Document(collection = "bookings")
public class BookingDocument {

    @Id
    private String id;              // 01JB0001
    private String code;            // BK-20261120-0001
    private String facilityId;
    private String courtId;
    private String sportCode;
    private String userId;
    private String createdBy;
    private String source;          // ONLINE | COUNTER
    private String bookingDate;     // "2026-11-20"
    private List<String> slotStarts;// ["18:00", "19:00"]
    private Instant startAt;
    private Instant endAt;
    private int players;
    private String status;          // HELD | CONFIRMED | CHECKED_IN | COMPLETED | CANCELED | NO_SHOW | EXPIRED
    private Pricing pricing;
    private PolicySnapshot policySnapshot;
    private Instant holdExpiresAt;
    private Instant checkedInAt;
    private NoShowInfo noShow;
    private CancelInfo cancel;
    private String rescheduledFrom;
    private String matchId;
    private String note;
    private int version;
    private Instant createdAt;
    private Instant updatedAt;

    public static class Pricing {
        private List<PriceLine> lines;
        private long totalVnd;
        private long depositVnd;
        private long paidVnd;

        public Pricing() {}

        public Pricing(List<PriceLine> lines, long totalVnd, long depositVnd, long paidVnd) {
            this.lines = lines;
            this.totalVnd = totalVnd;
            this.depositVnd = depositVnd;
            this.paidVnd = paidVnd;
        }

        public List<PriceLine> getLines() { return lines; }
        public void setLines(List<PriceLine> lines) { this.lines = lines; }
        public long getTotalVnd() { return totalVnd; }
        public void setTotalVnd(long totalVnd) { this.totalVnd = totalVnd; }
        public long getDepositVnd() { return depositVnd; }
        public void setDepositVnd(long depositVnd) { this.depositVnd = depositVnd; }
        public long getPaidVnd() { return paidVnd; }
        public void setPaidVnd(long paidVnd) { this.paidVnd = paidVnd; }
    }

    public static class PriceLine {
        private String slot;
        private long priceVnd;

        public PriceLine() {}

        public PriceLine(String slot, long priceVnd) {
            this.slot = slot;
            this.priceVnd = priceVnd;
        }

        public String getSlot() { return slot; }
        public void setSlot(String slot) { this.slot = slot; }
        public long getPriceVnd() { return priceVnd; }
        public void setPriceVnd(long priceVnd) { this.priceVnd = priceVnd; }
    }

    public static class PolicySnapshot {
        private String policyId;
        private int version;
        private int depositPercent;
        private int graceMinutes;
        private List<PolicyDocument.CancelTier> cancelTiers;

        public PolicySnapshot() {}

        public PolicySnapshot(String policyId, int version, int depositPercent, int graceMinutes,
                              List<PolicyDocument.CancelTier> cancelTiers) {
            this.policyId = policyId;
            this.version = version;
            this.depositPercent = depositPercent;
            this.graceMinutes = graceMinutes;
            this.cancelTiers = cancelTiers;
        }

        public String getPolicyId() { return policyId; }
        public void setPolicyId(String policyId) { this.policyId = policyId; }
        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
        public int getDepositPercent() { return depositPercent; }
        public void setDepositPercent(int depositPercent) { this.depositPercent = depositPercent; }
        public int getGraceMinutes() { return graceMinutes; }
        public void setGraceMinutes(int graceMinutes) { this.graceMinutes = graceMinutes; }
        public List<PolicyDocument.CancelTier> getCancelTiers() { return cancelTiers; }
        public void setCancelTiers(List<PolicyDocument.CancelTier> cancelTiers) { this.cancelTiers = cancelTiers; }
    }

    public static class NoShowInfo {
        private Instant markedAt;
        private String markedBy;
        private boolean excused;
        private String excuseNote;

        public Instant getMarkedAt() { return markedAt; }
        public void setMarkedAt(Instant markedAt) { this.markedAt = markedAt; }
        public String getMarkedBy() { return markedBy; }
        public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }
        public boolean isExcused() { return excused; }
        public void setExcused(boolean excused) { this.excused = excused; }
        public String getExcuseNote() { return excuseNote; }
        public void setExcuseNote(String excuseNote) { this.excuseNote = excuseNote; }
    }

    public static class CancelInfo {
        private Instant at;
        private String by;
        private long refundVnd;
        private String reason;

        public Instant getAt() { return at; }
        public void setAt(Instant at) { this.at = at; }
        public String getBy() { return by; }
        public void setBy(String by) { this.by = by; }
        public long getRefundVnd() { return refundVnd; }
        public void setRefundVnd(long refundVnd) { this.refundVnd = refundVnd; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }
    public String getSportCode() { return sportCode; }
    public void setSportCode(String sportCode) { this.sportCode = sportCode; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public List<String> getSlotStarts() { return slotStarts; }
    public void setSlotStarts(List<String> slotStarts) { this.slotStarts = slotStarts; }
    public Instant getStartAt() { return startAt; }
    public void setStartAt(Instant startAt) { this.startAt = startAt; }
    public Instant getEndAt() { return endAt; }
    public void setEndAt(Instant endAt) { this.endAt = endAt; }
    public int getPlayers() { return players; }
    public void setPlayers(int players) { this.players = players; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Pricing getPricing() { return pricing; }
    public void setPricing(Pricing pricing) { this.pricing = pricing; }
    public PolicySnapshot getPolicySnapshot() { return policySnapshot; }
    public void setPolicySnapshot(PolicySnapshot policySnapshot) { this.policySnapshot = policySnapshot; }
    public Instant getHoldExpiresAt() { return holdExpiresAt; }
    public void setHoldExpiresAt(Instant holdExpiresAt) { this.holdExpiresAt = holdExpiresAt; }
    public Instant getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(Instant checkedInAt) { this.checkedInAt = checkedInAt; }
    public NoShowInfo getNoShow() { return noShow; }
    public void setNoShow(NoShowInfo noShow) { this.noShow = noShow; }
    public CancelInfo getCancel() { return cancel; }
    public void setCancel(CancelInfo cancel) { this.cancel = cancel; }
    public String getRescheduledFrom() { return rescheduledFrom; }
    public void setRescheduledFrom(String rescheduledFrom) { this.rescheduledFrom = rescheduledFrom; }
    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
