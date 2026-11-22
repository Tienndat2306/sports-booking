package com.huit.sportsbooking.repository.redis;

import com.huit.sportsbooking.model.redis.RedisKeys;
import java.util.Collections;
import java.util.List;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * Thực thi 4 script Lua nguyên tử trên Redis (Mục 3.7.3).
 */
@Component
public class RedisLuaExecutor {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> holdSlotsScript;
    private final DefaultRedisScript<Long> releaseHoldScript;
    private final DefaultRedisScript<Long> rateLimitScript;
    @SuppressWarnings("rawtypes")
    private final DefaultRedisScript<List> popDueScript;

    @SuppressWarnings("rawtypes")
    public RedisLuaExecutor(
            StringRedisTemplate redisTemplate,
            DefaultRedisScript<Long> holdSlotsScript,
            DefaultRedisScript<Long> releaseHoldScript,
            DefaultRedisScript<Long> rateLimitScript,
            DefaultRedisScript<List> popDueScript
    ) {
        this.redisTemplate = redisTemplate;
        this.holdSlotsScript = holdSlotsScript;
        this.releaseHoldScript = releaseHoldScript;
        this.rateLimitScript = rateLimitScript;
        this.popDueScript = popDueScript;
    }

    /**
     * Giữ tất cả các khung giờ hoặc không giữ khung nào (hold_slots.lua).
     *
     * @return true nếu giữ chỗ thành công toàn bộ khung, false nếu có ít nhất 1 khung đang bị giữ
     */
    public boolean holdSlots(String courtId, String date, List<String> slotStarts, String holdId, int ttlSeconds) {
        List<String> keys = RedisKeys.holdSlots(courtId, date, slotStarts);
        Long result = redisTemplate.execute(holdSlotsScript, keys, holdId, String.valueOf(ttlSeconds));
        return Long.valueOf(1L).equals(result);
    }

    /**
     * Giải phóng các khóa giữ chỗ thuộc về đúng holdId (release_hold.lua).
     *
     * @return số lượng khung đã được giải phóng
     */
    public long releaseHold(String courtId, String date, List<String> slotStarts, String holdId) {
        List<String> keys = RedisKeys.holdSlots(courtId, date, slotStarts);
        Long result = redisTemplate.execute(releaseHoldScript, keys, holdId);
        return result != null ? result : 0L;
    }

    /**
     * Tăng bộ đếm giới hạn tần suất và đặt TTL (rate_limit.lua).
     *
     * @return số lần gọi hiện tại trong cửa sổ thời gian
     */
    public long incrementRateLimit(String key, int windowSeconds) {
        Long count = redisTemplate.execute(rateLimitScript, Collections.singletonList(key), String.valueOf(windowSeconds));
        return count != null ? count : 0L;
    }

    /**
     * Lấy và xóa các phần tử đã đến hạn từ hàng đợi Sorted Set (pop_due.lua).
     */
    @SuppressWarnings("unchecked")
    public List<String> popDueItems(String queueKey, long nowEpochSeconds, int maxItems) {
        List<String> items = redisTemplate.execute(
                popDueScript,
                Collections.singletonList(queueKey),
                String.valueOf(nowEpochSeconds),
                String.valueOf(maxItems)
        );
        return items != null ? items : Collections.emptyList();
    }
}
