package com.huit.sportsbooking.controller.api;

import com.huit.sportsbooking.common.SseHub;
import com.huit.sportsbooking.common.UserContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Điểm cuối SSE (Server-Sent Events) dùng chung cho Thành viên A (cập nhật lịch trống)
 * và Thành viên C (thông báo cá nhân thời gian thực).
 */
@RestController
@RequestMapping("/api/v1/sse")
public class SseApiController {

    private final SseHub sseHub;

    public SseApiController(SseHub sseHub) {
        this.sseHub = sseHub;
    }

    /**
     * Lắng nghe thay đổi trạng thái khung giờ (FREE / HELD / BOOKED / MAINTENANCE) theo cơ sở.
     * GET /api/v1/sse/schedule?facilityId=FAC01
     */
    @GetMapping(value = "/schedule", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSchedule(@RequestParam(value = "facilityId", defaultValue = "FAC01") String facilityId) {
        return sseHub.subscribeSchedule(facilityId);
    }

    /**
     * Lắng nghe thông báo cá nhân thời gian thực (Waitlist, Bảo trì, Ghép kèo).
     * GET /api/v1/sse/me
     */
    @GetMapping(value = "/me", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUserNotifications(
            @RequestParam(value = "userId", required = false) String userIdParam) {
        String userId = (userIdParam != null && !userIdParam.isBlank())
                ? userIdParam
                : UserContext.getCurrentUserId();
        return sseHub.subscribeUser(userId);
    }
}
