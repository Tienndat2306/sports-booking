package com.huit.sportsbooking.dto.account;

/**
 * DTO yêu cầu đăng nhập / đăng ký bằng tài khoản Google.
 */
public record GoogleLoginRequest(
        String idToken,
        String email,
        String name,
        String avatarUrl,
        String role
) {}
