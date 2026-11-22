package com.huit.sportsbooking.model.cassandra;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Bảng Q8: audit_by_day_type (Mục 3.5.2).
 */
@Table("audit_by_day_type")
public class AuditByDayType {

    @PrimaryKeyColumn(name = "day", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private LocalDate day;

    @PrimaryKeyColumn(name = "event_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String eventType;       // LOGIN | LOGOUT | BOOKING_CREATE | BOOKING_CANCEL | PAYMENT | DATA_CHANGE

    @PrimaryKeyColumn(name = "event_time", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID eventTime;

    @Column("user_id")
    private String userId;

    @Column("role")
    private String role;

    @Column("entity_type")
    private String entityType;

    @Column("entity_id")
    private String entityId;

    @Column("detail")
    private String detail;

    @Column("ip")
    private String ip;

    public LocalDate getDay() { return day; }
    public void setDay(LocalDate day) { this.day = day; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public UUID getEventTime() { return eventTime; }
    public void setEventTime(UUID eventTime) { this.eventTime = eventTime; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
}
