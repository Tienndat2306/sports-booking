package com.huit.sportsbooking.model.cassandra;

import java.time.LocalDate;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Bảng Q6: revenue_daily (Mục 3.5.2) - Bảng bộ đếm (counter).
 */
@Table("revenue_daily")
public class RevenueDaily {

    @PrimaryKeyColumn(name = "facility_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String facilityId;

    @PrimaryKeyColumn(name = "month", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String month;           // '2026-11'

    @PrimaryKeyColumn(name = "day", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private LocalDate day;

    @PrimaryKeyColumn(name = "sport_code", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private String sportCode;

    @PrimaryKeyColumn(name = "court_id", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
    private String courtId;

    @Column("revenue_vnd")
    @CassandraType(type = CassandraType.Name.COUNTER)
    private Long revenueVnd;

    @Column("refund_vnd")
    @CassandraType(type = CassandraType.Name.COUNTER)
    private Long refundVnd;

    @Column("booking_count")
    @CassandraType(type = CassandraType.Name.COUNTER)
    private Long bookingCount;

    @Column("booked_slots")
    @CassandraType(type = CassandraType.Name.COUNTER)
    private Long bookedSlots;

    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public LocalDate getDay() { return day; }
    public void setDay(LocalDate day) { this.day = day; }
    public String getSportCode() { return sportCode; }
    public void setSportCode(String sportCode) { this.sportCode = sportCode; }
    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }
    public Long getRevenueVnd() { return revenueVnd; }
    public void setRevenueVnd(Long revenueVnd) { this.revenueVnd = revenueVnd; }
    public Long getRefundVnd() { return refundVnd; }
    public void setRefundVnd(Long refundVnd) { this.refundVnd = refundVnd; }
    public Long getBookingCount() { return bookingCount; }
    public void setBookingCount(Long bookingCount) { this.bookingCount = bookingCount; }
    public Long getBookedSlots() { return bookedSlots; }
    public void setBookedSlots(Long bookedSlots) { this.bookedSlots = bookedSlots; }
}
