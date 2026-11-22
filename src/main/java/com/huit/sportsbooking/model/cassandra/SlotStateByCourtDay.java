package com.huit.sportsbooking.model.cassandra;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Bảng Q1: slot_state_by_court_day (Mục 3.5.2).
 * Nguồn chuẩn quyết định một khung giờ đã bị chiếm (BOOKED | MAINTENANCE) hay chưa.
 */
@Table("slot_state_by_court_day")
public class SlotStateByCourtDay {

    @PrimaryKeyColumn(name = "court_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String courtId;

    @PrimaryKeyColumn(name = "day", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private LocalDate day;

    @PrimaryKeyColumn(name = "slot_start", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private LocalTime slotStart;

    @Column("status")
    private String status;          // BOOKED | MAINTENANCE

    @Column("booking_id")
    private String bookingId;

    @Column("maintenance_id")
    private String maintenanceId;

    @Column("user_id")
    private String userId;

    @Column("updated_at")
    private Instant updatedAt;

    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }
    public LocalDate getDay() { return day; }
    public void setDay(LocalDate day) { this.day = day; }
    public LocalTime getSlotStart() { return slotStart; }
    public void setSlotStart(LocalTime slotStart) { this.slotStart = slotStart; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getMaintenanceId() { return maintenanceId; }
    public void setMaintenanceId(String maintenanceId) { this.maintenanceId = maintenanceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
