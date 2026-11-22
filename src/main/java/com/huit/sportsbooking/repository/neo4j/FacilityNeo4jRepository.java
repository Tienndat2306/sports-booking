package com.huit.sportsbooking.repository.neo4j;

import com.huit.sportsbooking.model.neo4j.FacilityNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FacilityNeo4jRepository extends Neo4jRepository<FacilityNode, String> {
}
