package com.huit.sportsbooking.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Tiện ích lấy ngữ cảnh người dùng đang đăng nhập trong môi trường phát triển (Dev).
 * Giúp các thành viên phát triển Module 2, 3, 4 lấy ngay được User ID và Role hiện tại
 * mà không bị nghẽn chờ đợi màn hình đăng nhập hoàn chỉnh.
 *
 * Cách dùng:
 * - Mặc định trả về CUSTOMER mẫu (01JU0001).
 * - Khi test qua Postman / cURL, có thể truyền Header "X-User-Id" hoặc "X-User-Role" tùy ý.
 */
public final class UserContext {

    public static final String DEFAULT_DEV_USER_ID = "01JU0001"; // Khách hàng mẫu trong seed data
    public static final String DEFAULT_DEV_ROLE = "CUSTOMER";

    private UserContext() {}

    public static String getCurrentUserId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String headerVal = request.getHeader("X-User-Id");
            if (headerVal != null && !headerVal.isBlank()) {
                return headerVal.trim();
            }
        }
        return DEFAULT_DEV_USER_ID;
    }

    public static String getCurrentRole() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String headerVal = request.getHeader("X-User-Role");
            if (headerVal != null && !headerVal.isBlank()) {
                return headerVal.trim().toUpperCase();
            }
        }
        return DEFAULT_DEV_ROLE;
    }
}
