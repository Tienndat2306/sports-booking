package com.huit.sportsbooking.repository.neo4j;

import com.huit.sportsbooking.model.neo4j.PlayerNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PlayerNeo4jRepository extends Neo4jRepository<PlayerNode, String> {
}
