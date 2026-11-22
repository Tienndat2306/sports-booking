package com.huit.sportsbooking.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Trung tâm quản lý kết nối Server-Sent Events (SSE) dùng chung cho cả nhóm (Mục 3.2 & 8.2).
 * - Thành viên A dùng để đẩy trạng thái đổi màu khung giờ (HELD / BOOKED / FREE) theo cơ sở.
 * - Thành viên C dùng để đẩy thông báo thời gian thực (Waitlist có chỗ, Bảo trì, Ghép kèo) tới từng người dùng.
 */
@Component
public class SseHub {

    private static final Logger log = LoggerFactory.getLogger(SseHub.class);
    private static final long DEFAULT_TIMEOUT_MS = 30 * 60 * 1000L; // 30 phút

    private final Map<String, List<SseEmitter>> facilityScheduleEmitters = new ConcurrentHashMap<>();
    private final Map<String, List<SseEmitter>> userNotificationEmitters = new ConcurrentHashMap<>();

    /**
     * Đăng ký nhận cập nhật lịch sân thời gian thực theo mã cơ sở (/api/v1/sse/schedule?facilityId=FAC01).
     */
    public SseEmitter subscribeSchedule(String facilityId) {
        String key = (facilityId != null && !facilityId.isBlank()) ? facilityId : "ALL";
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT_MS);
        List<SseEmitter> list = facilityScheduleEmitters.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        emitter.onCompletion(() -> list.remove(emitter));
        emitter.onTimeout(() -> list.remove(emitter));
        emitter.onError(e -> list.remove(emitter));

        sendSafe(emitter, "connected", Map.of("facilityId", key, "status", "SUBSCRIBED"));
        return emitter;
    }

    /**
     * Đăng ký nhận thông báo cá nhân thời gian thực (/api/v1/sse/me).
     */
    public SseEmitter subscribeUser(String userId) {
        String key = (userId != null && !userId.isBlank()) ? userId : "ANONYMOUS";
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT_MS);
        List<SseEmitter> list = userNotificationEmitters.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        list.add(emitter);

        emitter.onCompletion(() -> list.remove(emitter));
        emitter.onTimeout(() -> list.remove(emitter));
        emitter.onError(e -> list.remove(emitter));

        sendSafe(emitter, "connected", Map.of("userId", key, "status", "SUBSCRIBED"));
        return emitter;
    }

    /**
     * Phát sự kiện thay đổi trạng thái khung giờ tới mọi trình duyệt đang xem lịch của cơ sở.
     */
    public void broadcastScheduleUpdate(String facilityId, Object data) {
        broadcastToGroup(facilityScheduleEmitters.get(facilityId), "schedule-update", data);
        broadcastToGroup(facilityScheduleEmitters.get("ALL"), "schedule-update", data);
    }

    /**
     * Gửi thông báo thời gian thực tới một người dùng cụ thể.
     */
    public void sendToUser(String userId, String eventName, Object data) {
        broadcastToGroup(userNotificationEmitters.get(userId), eventName, data);
    }

    private void broadcastToGroup(List<SseEmitter> emitters, String eventName, Object data) {
        if (emitters == null || emitters.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            if (!sendSafe(emitter, eventName, data)) {
                emitters.remove(emitter);
            }
        }
    }

    private boolean sendSafe(SseEmitter emitter, String eventName, Object data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
            return true;
        } catch (IOException | IllegalStateException e) {
            log.debug("Kết nối SSE đã đóng khi gửi sự kiện [{}]: {}", eventName, e.getMessage());
            return false;
        }
    }
}
