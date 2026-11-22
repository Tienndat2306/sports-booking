package com.huit.sportsbooking.model.mongo;

import java.time.Instant;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection: notifications (Mục 3.4.3).
 * Tự động xóa sau 90 ngày nhờ TTL index trên createdAt.
 */
@Document(collection = "notifications")
public class NotificationDocument {

    @Id
    private String id;              // 01JN0001
    private String userId;
    private String type;            // WAITLIST_SLOT_FREE | MAINTENANCE_AFFECTED | BOOKING_CONFIRMED...
    private String title;
    private Map<String, Object> refs;
    private Instant readAt;
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Map<String, Object> getRefs() { return refs; }
    public void setRefs(Map<String, Object> refs) { this.refs = refs; }
    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
