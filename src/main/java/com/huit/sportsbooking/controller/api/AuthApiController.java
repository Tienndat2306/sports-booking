package com.huit.sportsbooking.controller.api;

import com.huit.sportsbooking.common.ApiResponse;
import com.huit.sportsbooking.common.UserContext;
import com.huit.sportsbooking.dto.account.AuthResponse;
import com.huit.sportsbooking.dto.account.GoogleLoginRequest;
import com.huit.sportsbooking.model.mongo.UserDocument;
import com.huit.sportsbooking.service.account.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API phục vụ Đăng nhập / Đăng ký qua Google (Module 1 - UC01, UC02).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final AccountService accountService;

    public AuthApiController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Đăng nhập hoặc tự động Đăng ký bằng tài khoản Google.
     * POST /api/v1/auth/google
     */
    @PostMapping("/google")
    public ApiResponse<AuthResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isBlank()) {
            clientIp = httpRequest.getRemoteAddr();
        }
        AuthResponse response = accountService.loginOrRegisterGoogle(request, clientIp);
        return ApiResponse.ok("Đăng nhập bằng tài khoản Google thành công", response);
    }

    /**
     * Lấy thông tin tài khoản hiện tại của người dùng.
     * GET /api/v1/auth/me
     */
    @GetMapping("/me")
    public ApiResponse<UserDocument> getCurrentUser() {
        String userId = UserContext.getCurrentUserId();
        UserDocument user = accountService.getCurrentUserProfile(userId);
        return ApiResponse.ok("Lấy thông tin tài khoản thành công", user);
    }

    /**
     * Đăng xuất khỏi hệ thống.
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String token,
                                    HttpServletRequest httpRequest) {
        String userId = UserContext.getCurrentUserId();
        String clientIp = httpRequest.getRemoteAddr();
        accountService.logout(userId, token, clientIp);
        return ApiResponse.ok("Đăng xuất thành công", null);
    }
}
