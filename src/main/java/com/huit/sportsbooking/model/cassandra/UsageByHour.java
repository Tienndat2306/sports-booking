package com.huit.sportsbooking.model.cassandra;

import java.time.LocalDate;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Bảng Q7: usage_by_hour (Mục 3.5.2) - Bảng bộ đếm (counter).
 */
@Table("usage_by_hour")
public class UsageByHour {

    @PrimaryKeyColumn(name = "facility_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String facilityId;

    @PrimaryKeyColumn(name = "month", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String month;           // '2026-11'

    @PrimaryKeyColumn(name = "hour", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private int hour;

    @PrimaryKeyColumn(name = "day", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private LocalDate day;

    @Column("booked_slots")
    @CassandraType(type = CassandraType.Name.COUNTER)
    private Long bookedSlots;

    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }
    public LocalDate getDay() { return day; }
    public void setDay(LocalDate day) { this.day = day; }
    public Long getBookedSlots() { return bookedSlots; }
    public void setBookedSlots(Long bookedSlots) { this.bookedSlots = bookedSlots; }
}
