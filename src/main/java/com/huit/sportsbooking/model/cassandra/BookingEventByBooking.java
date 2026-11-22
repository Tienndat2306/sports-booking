package com.huit.sportsbooking.model.cassandra;

import java.util.Map;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Bảng Q3: booking_events_by_booking (Mục 3.5.2).
 * Nhật ký chuyển trạng thái của đơn (append-only).
 */
@Table("booking_events_by_booking")
public class BookingEventByBooking {

    @PrimaryKeyColumn(name = "booking_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String bookingId;

    @PrimaryKeyColumn(name = "event_time", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private UUID eventTime;         // timeuuid

    @Column("event_type")
    private String eventType;

    @Column("actor_id")
    private String actorId;

    @Column("from_status")
    private String fromStatus;

    @Column("to_status")
    private String toStatus;

    @Column("detail")
    private Map<String, String> detail;

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public UUID getEventTime() { return eventTime; }
    public void setEventTime(UUID eventTime) { this.eventTime = eventTime; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }
    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }
    public Map<String, String> getDetail() { return detail; }
    public void setDetail(Map<String, String> detail) { this.detail = detail; }
}
