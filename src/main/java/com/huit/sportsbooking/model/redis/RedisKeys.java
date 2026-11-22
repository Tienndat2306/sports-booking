package com.huit.sportsbooking.model.redis;

import java.util.List;

/**
 * Quy ước đặt tên khóa Redis dùng chung toàn hệ thống (Mục 3.7.1 & 3.7.2).
 * Các thành viên bắt buộc dùng class này thay vì tự ghép chuỗi khóa thủ công.
 */
public final class RedisKeys {

    public static final String STREAM_EVENTS = "sb:stream:events";
    public static final String QUEUE_HOLD_EXP = "sb:q:holdexp";
    public static final String QUEUE_NOSHOW = "sb:q:noshow";

    public static final String GROUP_NEO4J = "cg-neo4j";
    public static final String GROUP_STATS = "cg-stats";
    public static final String GROUP_CACHE = "cg-cache";

    private RedisKeys() {
    }

    /**
     * Khóa giữ chỗ một khung giờ: sb:hold:{<sân>:<ngày>}:<HH:mm>
     * Dùng hash tag {<sân>:<ngày>} để các khung giờ cùng sân-ngày nằm chung Redis Cluster slot.
     */
    public static String holdSlot(String courtId, String date, String slotStart) {
        return "sb:hold:{" + courtId + ":" + date + "}:" + slotStart;
    }

    /**
     * Danh sách khóa giữ chỗ cho nhiều khung giờ của cùng 1 sân trong 1 ngày.
     */
    public static List<String> holdSlots(String courtId, String date, List<String> slotStarts) {
        return slotStarts.stream()
                .map(slot -> holdSlot(courtId, date, slot))
                .toList();
    }

    /**
     * Phiên giữ chỗ (Hash): sb:holdses:<holdId>
     */
    public static String holdSession(String holdId) {
        return "sb:holdses:" + holdId;
    }

    /**
     * Cache lịch trống theo cơ sở và ngày (Hash): sb:cache:sched:<cơ sở>:<ngày>
     */
    public static String cacheSchedule(String facilityId, String date) {
        return "sb:cache:sched:" + facilityId + ":" + date;
    }

    /**
     * Cache bảng giá: sb:cache:price:<cơ sở>:<môn>:<loại ngày>
     */
    public static String cachePrice(String facilityId, String sportCode, String dayType) {
        return "sb:cache:price:" + facilityId + ":" + sportCode + ":" + dayType;
    }

    /**
     * Cache chính sách hiện hành của cơ sở: sb:cache:policy:<cơ sở>
     */
    public static String cachePolicy(String facilityId) {
        return "sb:cache:policy:" + facilityId;
    }

    /**
     * Cache kết quả gợi ý từ Neo4j: sb:cache:rec:<userId>:<môn>
     */
    public static String cacheRecommendation(String userId, String sportCode) {
        return "sb:cache:rec:" + userId + ":" + sportCode;
    }

    /**
     * Danh sách chờ khung giờ (Sorted Set): sb:wait:{<sân>:<ngày>}:<HH:mm>
     */
    public static String waitlist(String courtId, String date, String slotStart) {
        return "sb:wait:{" + courtId + ":" + date + "}:" + slotStart;
    }

    /**
     * Giới hạn tần suất giữ chỗ: sb:rl:hold:<userId>
     */
    public static String rateLimitHold(String userId) {
        return "sb:rl:hold:" + userId;
    }

    /**
     * Giới hạn tần suất đăng nhập sai: sb:rl:login:<ip>
     */
    public static String rateLimitLogin(String ip) {
        return "sb:rl:login:" + ip;
    }

    /**
     * Bảng xếp hạng sân theo cơ sở trong tháng: sb:rank:court:<cơ sở>:<yyyyMM>
     */
    public static String rankCourtFacility(String facilityId, String yearMonth) {
        return "sb:rank:court:" + facilityId + ":" + yearMonth;
    }

    /**
     * Bảng xếp hạng sân toàn hệ thống: sb:rank:court:ALL:<yyyyMM>
     */
    public static String rankCourtAll(String yearMonth) {
        return "sb:rank:court:ALL:" + yearMonth;
    }

    /**
     * Kênh Pub/Sub cập nhật lịch theo cơ sở: sb:ch:sched:<cơ sở>
     */
    public static String channelSchedule(String facilityId) {
        return "sb:ch:sched:" + facilityId;
    }

    /**
     * Kênh Pub/Sub thông báo cá nhân: sb:ch:user:<userId>
     */
    public static String channelUser(String userId) {
        return "sb:ch:user:" + userId;
    }

    /**
     * Thu hồi JWT token: sb:auth:revoked:<jti>
     */
    public static String authRevoked(String jti) {
        return "sb:auth:revoked:" + jti;
    }

    /**
     * Khóa chống lặp (Idempotency): sb:idem:<phạm vi>:<khóa>
     */
    public static String idempotency(String scope, String key) {
        return "sb:idem:" + scope + ":" + key;
    }
}
