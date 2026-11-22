package com.huit.sportsbooking.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Node: Court (Mục 3.6.1).
 */
@Node("Court")
public class CourtNode {

    @Id
    private String id;
    private String name;
    private String sportCode;

    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.OUTGOING)
    private FacilityNode facility;

    @Relationship(type = "OF_SPORT", direction = Relationship.Direction.OUTGOING)
    private SportNode sport;

    public CourtNode() {}

    public CourtNode(String id, String name, String sportCode) {
        this.id = id;
        this.name = name;
        this.sportCode = sportCode;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSportCode() { return sportCode; }
    public void setSportCode(String sportCode) { this.sportCode = sportCode; }
    public FacilityNode getFacility() { return facility; }
    public void setFacility(FacilityNode facility) { this.facility = facility; }
    public SportNode getSport() { return sport; }
    public void setSport(SportNode sport) { this.sport = sport; }
}
