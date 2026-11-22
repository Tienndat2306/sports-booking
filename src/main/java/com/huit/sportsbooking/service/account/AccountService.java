package com.huit.sportsbooking.service.account;

import com.huit.sportsbooking.dto.account.AuthResponse;
import com.huit.sportsbooking.dto.account.GoogleLoginRequest;
import com.huit.sportsbooking.model.mongo.UserDocument;

/**
 * Service quản lý tài khoản người dùng (Module 1 - UC01, UC02).
 */
public interface AccountService {

    /**
     * Đăng nhập hoặc tự động Đăng ký nếu tài khoản Google chưa từng tồn tại (UC01 & UC02).
     * - Lưu User vào MongoDB collection users.
     * - Đồng bộ tạo Node Player vào đồ thị Neo4j.
     * - Ghi nhật ký kiểm toán vào Cassandra audit_by_day_type.
     */
    AuthResponse loginOrRegisterGoogle(GoogleLoginRequest request, String ipAddress);

    /**
     * Lấy thông tin hồ sơ người dùng hiện tại (UC03).
     */
    UserDocument getCurrentUserProfile(String userId);

    /**
     * Đăng xuất và ghi nhận nhật ký kiểm toán.
     */
    void logout(String userId, String token, String ipAddress);
}
