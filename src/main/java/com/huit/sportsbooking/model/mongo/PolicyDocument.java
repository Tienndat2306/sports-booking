package com.huit.sportsbooking.model.mongo;

import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection: policies (Mục 3.4.3).
 * Có $jsonSchema kiểm tra trên MongoDB.
 */
@Document(collection = "policies")
public class PolicyDocument {

    @Id
    private String id;              // POL-FAC01-v1
    private String facilityId;
    private int version;
    private boolean current;
    private int depositPercent;
    private int holdMinutes;
    private int graceMinutes;
    private int noShowThreshold;
    private List<CancelTier> cancelTiers;
    private Instant effectiveFrom;
    private String createdBy;

    public static class CancelTier {
        private int beforeHours;
        private int refundPercent;

        public CancelTier() {}

        public CancelTier(int beforeHours, int refundPercent) {
            this.beforeHours = beforeHours;
            this.refundPercent = refundPercent;
        }

        public int getBeforeHours() { return beforeHours; }
        public void setBeforeHours(int beforeHours) { this.beforeHours = beforeHours; }
        public int getRefundPercent() { return refundPercent; }
        public void setRefundPercent(int refundPercent) { this.refundPercent = refundPercent; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }
    public int getDepositPercent() { return depositPercent; }
    public void setDepositPercent(int depositPercent) { this.depositPercent = depositPercent; }
    public int getHoldMinutes() { return holdMinutes; }
    public void setHoldMinutes(int holdMinutes) { this.holdMinutes = holdMinutes; }
    public int getGraceMinutes() { return graceMinutes; }
    public void setGraceMinutes(int graceMinutes) { this.graceMinutes = graceMinutes; }
    public int getNoShowThreshold() { return noShowThreshold; }
    public void setNoShowThreshold(int noShowThreshold) { this.noShowThreshold = noShowThreshold; }
    public List<CancelTier> getCancelTiers() { return cancelTiers; }
    public void setCancelTiers(List<CancelTier> cancelTiers) { this.cancelTiers = cancelTiers; }
    public Instant getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(Instant effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
