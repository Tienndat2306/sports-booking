package com.huit.sportsbooking.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Sinh mã định danh thống nhất dùng chung trên cả 4 CSDL (mục 3.1.2).
 */
public final class IdGenerator {

    public static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE_COMPACT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private IdGenerator() {
    }

    /**
     * Sinh ID ngắn gọn có thứ tự thời gian (tương tự ULID) cho User, Booking, Match...
     */
    public static String nextId(String prefix) {
        String ts = Long.toString(System.currentTimeMillis(), 36).toUpperCase();
        String rand = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return (prefix != null && !prefix.isBlank()) ? prefix + "-" + ts + rand : ts + rand;
    }

    /**
     * Sinh mã đơn hiển thị cho khách (ví dụ: BK-20261120-4821).
     */
    public static String nextBookingCode(LocalDate bookingDate) {
        LocalDate date = bookingDate != null ? bookingDate : LocalDate.now(VN_ZONE);
        int suffix = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "BK-" + date.format(DATE_COMPACT) + "-" + suffix;
    }
}
