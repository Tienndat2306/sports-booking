package com.huit.sportsbooking.model.mongo;

import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Collection: courts (Mục 3.4.3).
 * Trường attributes linh hoạt theo từng môn thể thao (NFR04).
 */
@Document(collection = "courts")
public class CourtDocument {

    @Id
    private String id;              // FAC01-FB-01, FAC01-BM-01...
    private String facilityId;
    private String sportCode;       // FOOTBALL5 | BADMINTON | VOLLEYBALL
    private String name;
    private String status;          // ACTIVE | MAINTENANCE | INACTIVE
    private int maxPlayers;
    private Map<String, Object> attributes;
    private List<String> amenities;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public String getSportCode() { return sportCode; }
    public void setSportCode(String sportCode) { this.sportCode = sportCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
}
