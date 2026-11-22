package com.huit.sportsbooking.repository.neo4j;

import com.huit.sportsbooking.model.neo4j.CourtNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CourtNeo4jRepository extends Neo4jRepository<CourtNode, String> {
}
