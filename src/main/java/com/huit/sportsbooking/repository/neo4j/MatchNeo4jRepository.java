package com.huit.sportsbooking.repository.neo4j;

import com.huit.sportsbooking.model.neo4j.MatchNode;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MatchNeo4jRepository extends Neo4jRepository<MatchNode, String> {
    List<MatchNode> findByStatusAndSportCodeOrderByStartAtAsc(String status, String sportCode);
}
