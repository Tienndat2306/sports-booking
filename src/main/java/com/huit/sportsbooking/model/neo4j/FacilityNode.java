package com.huit.sportsbooking.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Node: Facility (Mục 3.6.1).
 * 2 nút: FAC01, FAC02.
 */
@Node("Facility")
public class FacilityNode {

    @Id
    private String id;
    private String name;

    public FacilityNode() {}

    public FacilityNode(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
