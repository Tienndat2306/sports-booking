package com.huit.sportsbooking.model.mongo;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection: price_rules (Mục 3.4.3).
 */
@Document(collection = "price_rules")
public class PriceRuleDocument {

    @Id
    private String id;              // PR-FAC01-FOOTBALL5-WD-PEAK
    private String facilityId;
    private String sportCode;
    private String courtId;         // null nếu áp dụng chung cho mọi sân cùng môn của cơ sở
    private String dayType;         // WEEKDAY | WEEKEND | HOLIDAY
    private String band;            // PEAK | OFFPEAK
    private String fromTime;        // "17:00"
    private String toTime;          // "22:00"
    private long priceVnd;
    private Instant validFrom;
    private Instant validTo;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public String getSportCode() { return sportCode; }
    public void setSportCode(String sportCode) { this.sportCode = sportCode; }
    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }
    public String getDayType() { return dayType; }
    public void setDayType(String dayType) { this.dayType = dayType; }
    public String getBand() { return band; }
    public void setBand(String band) { this.band = band; }
    public String getFromTime() { return fromTime; }
    public void setFromTime(String fromTime) { this.fromTime = fromTime; }
    public String getToTime() { return toTime; }
    public void setToTime(String toTime) { this.toTime = toTime; }
    public long getPriceVnd() { return priceVnd; }
    public void setPriceVnd(long priceVnd) { this.priceVnd = priceVnd; }
    public Instant getValidFrom() { return validFrom; }
    public void setValidFrom(Instant validFrom) { this.validFrom = validFrom; }
    public Instant getValidTo() { return validTo; }
    public void setValidTo(Instant validTo) { this.validTo = validTo; }
}
