package com.huit.sportsbooking.model.mongo;

import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection: categories (Mục 3.4.3).
 * type: SPORT | SURFACE | SLOT_TEMPLATE | AMENITY
 */
@Document(collection = "categories")
public class CategoryDocument {

    @Id
    private String id;              // SPORT:FOOTBALL5
    private String type;            // SPORT | SURFACE | SLOT_TEMPLATE | AMENITY
    private String code;            // FOOTBALL5
    private String name;            // Bóng đá 5 người
    private boolean active;
    private Map<String, Object> meta;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Map<String, Object> getMeta() { return meta; }
    public void setMeta(Map<String, Object> meta) { this.meta = meta; }
}
