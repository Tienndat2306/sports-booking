package com.huit.sportsbooking.dto.account;

public record RegisterRequest(
        String fullName,
        String phone,
        String email,
        String password
) {}
