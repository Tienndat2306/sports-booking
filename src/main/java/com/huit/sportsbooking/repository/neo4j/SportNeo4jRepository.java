package com.huit.sportsbooking.repository.neo4j;

import com.huit.sportsbooking.model.neo4j.SportNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SportNeo4jRepository extends Neo4jRepository<SportNode, String> {
}
