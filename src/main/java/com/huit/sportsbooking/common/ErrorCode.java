package com.huit.sportsbooking.common;

import org.springframework.http.HttpStatus;

/**
 * Danh mục mã lỗi dùng chung cho toàn bộ các module.
 */
public enum ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Chưa xác thực hoặc phiên đăng nhập hết hạn"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Không có quyền thực hiện thao tác này"),
    SLOT_ALREADY_HELD(HttpStatus.CONFLICT, "Khung giờ đang được người khác giữ chỗ"),
    SLOT_ALREADY_BOOKED(HttpStatus.CONFLICT, "Khung giờ đã có người đặt hoặc đang bảo trì"),
    HOLD_EXPIRED(HttpStatus.GONE, "Phiên giữ chỗ đã hết hạn"),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "Thao tác quá nhanh, vui lòng thử lại sau"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống nội bộ");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
