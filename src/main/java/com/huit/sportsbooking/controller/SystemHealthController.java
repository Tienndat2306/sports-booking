package com.huit.sportsbooking.controller;

import com.huit.sportsbooking.common.ApiResponse;
import com.huit.sportsbooking.repository.mongo.FacilityMongoRepository;
import com.huit.sportsbooking.repository.neo4j.SportNeo4jRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint kiểm tra kết nối đồng thời tới cả 4 hệ CSDL NoSQL (MongoDB, Cassandra, Neo4j, Redis).
 */
@RestController
@RequestMapping("/api/v1/system")
public class SystemHealthController {

    private final FacilityMongoRepository facilityMongoRepository;
    private final CqlTemplate cqlTemplate;
    private final SportNeo4jRepository sportNeo4jRepository;
    private final StringRedisTemplate stringRedisTemplate;

    public SystemHealthController(
            FacilityMongoRepository facilityMongoRepository,
            CqlTemplate cqlTemplate,
            SportNeo4jRepository sportNeo4jRepository,
            StringRedisTemplate stringRedisTemplate
    ) {
        this.facilityMongoRepository = facilityMongoRepository;
        this.cqlTemplate = cqlTemplate;
        this.sportNeo4jRepository = sportNeo4jRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> checkAllDatabases() {
        Map<String, Object> status = new LinkedHashMap<>();

        try {
            long facilities = facilityMongoRepository.count();
            status.put("mongodb", Map.of("status", "UP", "facilitiesCount", facilities));
        } catch (Exception e) {
            status.put("mongodb", errorDetail(e));
        }

        try {
            String releaseVersion = cqlTemplate.queryForObject("SELECT release_version FROM system.local", String.class);
            status.put("cassandra", Map.of("status", "UP", "version", releaseVersion != null ? releaseVersion : "OK"));
        } catch (Exception e) {
            status.put("cassandra", errorDetail(e));
        }

        try {
            long sports = sportNeo4jRepository.count();
            status.put("neo4j", Map.of("status", "UP", "sportsCount", sports));
        } catch (Exception e) {
            status.put("neo4j", errorDetail(e));
        }

        try {
            String pong = stringRedisTemplate.execute((RedisCallback<String>) RedisConnectionCommands::ping);
            status.put("redis", Map.of("status", "UP", "ping", pong != null ? pong : "PONG"));
        } catch (Exception e) {
            status.put("redis", errorDetail(e));
        }

        return ApiResponse.ok("Kiểm tra kết nối 4 hệ CSDL NoSQL", status);
    }

    private Map<String, Object> errorDetail(Exception e) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("status", "DOWN");
        detail.put("exception", e.getClass().getSimpleName());
        detail.put("error", e.getMessage() != null ? e.getMessage() : "Không có thông điệp chi tiết");
        return detail;
    }
}
