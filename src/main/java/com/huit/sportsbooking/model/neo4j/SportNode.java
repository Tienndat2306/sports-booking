package com.huit.sportsbooking.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Node: Sport (Mục 3.6.1).
 * 3 nút: FOOTBALL5, BADMINTON, VOLLEYBALL.
 */
@Node("Sport")
public class SportNode {

    @Id
    private String code;
    private String name;

    public SportNode() {}

    public SportNode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
