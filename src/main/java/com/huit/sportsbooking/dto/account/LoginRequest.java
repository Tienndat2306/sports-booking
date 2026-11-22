package com.huit.sportsbooking.dto.account;

public record LoginRequest(
        String phone,
        String password
) {}
