package com.huit.sportsbooking.dto.account;

public record AuthResponse(
        String token,
        String userId,
        String fullName,
        String phone,
        String role
) {}
