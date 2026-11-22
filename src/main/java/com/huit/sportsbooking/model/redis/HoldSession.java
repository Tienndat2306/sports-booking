package com.huit.sportsbooking.model.redis;

import java.time.Instant;
import java.util.List;

/**
 * Thông tin phiên giữ chỗ tạm thời lưu tại khóa Hash sb:holdses:<holdId> (Mục 3.7.2).
 */
public record HoldSession(
        String holdId,
        String userId,
        String bookingId,
        String courtId,
        String date,
        List<String> slots,
        long totalVnd,
        Instant expiresAt
) {
}
