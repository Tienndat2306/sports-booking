package com.huit.sportsbooking.common;

import com.huit.sportsbooking.model.redis.RedisKeys;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Thành phần dùng chung để phát sự kiện vào Redis Stream (sb:stream:events)
 * và kiểm tra chống lặp sự kiện (Idempotency - Mục 3.7.2 & NFR06).
 *
 * Tất cả các thành viên (A, B, C) dùng class này để đồng bộ dữ liệu bất đồng bộ
 * sang Neo4j (cg-neo4j), Cassandra Counters/Ranking (cg-stats) và xóa Cache (cg-cache).
 */
@Component
public class RedisStreamPublisher {

    private static final Logger log = LoggerFactory.getLogger(RedisStreamPublisher.class);

    private final StringRedisTemplate redisTemplate;

    public RedisStreamPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Phát một sự kiện chuẩn hóa vào Redis Stream `sb:stream:events`.
     *
     * @param eventType Loại sự kiện (ví dụ: BookingConfirmed, BookingCanceled, SlotReleased, UserUpdated, MaintenanceLocked)
     * @param payload   Các trường dữ liệu đi kèm (courtId, facilityId, userId, bookingId, amount, day...)
     * @return Mã eventId (ULID) vừa phát, hoặc null nếu Redis đang tạm ngưng (đảm bảo NFR05)
     */
    public String publishEvent(String eventType, Map<String, String> payload) {
        String eventId = IdGenerator.nextId("01JEVT");
        Map<String, String> body = new HashMap<>();
        if (payload != null) {
            body.putAll(payload);
        }
        body.put("eventId", eventId);
        body.put("eventType", eventType);
        body.put("timestamp", Instant.now().toString());

        try {
            StringRecord record = StreamRecords.string(body).withStreamKey(RedisKeys.STREAM_EVENTS);
            RecordId recordId = redisTemplate.opsForStream().add(record);
            log.info("Đã phát sự kiện [{}] vào Redis Stream ({}): eventId={}, recordId={}",
                    eventType, RedisKeys.STREAM_EVENTS, eventId, recordId);
            return eventId;
        } catch (Exception e) {
            log.warn("Không thể phát sự kiện [{}] vào Redis Stream (hệ thống vẫn tiếp tục theo NFR05): {}",
                    eventType, e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra và đánh dấu khóa chống lặp (Idempotency) bằng SET NX trước khi Consumer xử lý sự kiện
     * hoặc trước khi xử lý thanh toán (sb:idem:<scope>:<key>).
     *
     * @param scope      Phạm vi (ví dụ: "evt", "pay")
     * @param key        Mã định danh duy nhất (ví dụ: eventId hoặc paymentRequestId)
     * @param ttlSeconds Thời gian sống của khóa chống lặp (ví dụ: 86400s = 24 giờ)
     * @return true nếu đây là lần đầu tiên xử lý (hợp lệ), false nếu đã xử lý trước đó (bỏ qua)
     */
    public boolean tryAcquireIdempotency(String scope, String key, long ttlSeconds) {
        String redisKey = RedisKeys.idempotency(scope, key);
        try {
            Boolean acquired = redisTemplate.opsForValue()
                    .setIfAbsent(redisKey, "1", Duration.ofSeconds(ttlSeconds));
            return Boolean.TRUE.equals(acquired);
        } catch (Exception e) {
            log.warn("Không thể kiểm tra khóa chống lặp Redis [{}]: {}", redisKey, e.getMessage());
            return true;
        }
    }
}
