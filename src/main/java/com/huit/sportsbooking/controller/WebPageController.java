package com.huit.sportsbooking.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller phục vụ giao diện Web phân tách hoàn toàn giữa Khách hàng (User Portal - /)
 * và Quản trị viên (Admin Dashboard - /admin).
 */
@Controller
public class WebPageController {

    @Value("${app.auth.google-client-id:}")
    private String googleClientId;

    // =========================================================================
    // 1. CỔNG KHÁCH HÀNG (USER PORTAL - /)
    // =========================================================================
    @GetMapping({"/", "/search"})
    public String userHomePage(Model model) {
        model.addAttribute("initialTab", "home");
        return "index";
    }

    @GetMapping("/schedule")
    public String userSchedulePage(Model model) {
        model.addAttribute("initialTab", "schedule");
        return "index";
    }

    @GetMapping("/checkout")
    public String userCheckoutPage(Model model) {
        model.addAttribute("initialTab", "checkout");
        return "index";
    }

    @GetMapping("/bookings")
    public String userBookingsPage(Model model) {
        model.addAttribute("initialTab", "bookings");
        return "index";
    }

    @GetMapping("/community")
    public String userCommunityPage(Model model) {
        model.addAttribute("initialTab", "match");
        return "index";
    }

    @GetMapping("/profile")
    public String userProfilePage(Model model) {
        model.addAttribute("initialTab", "auth");
        return "index";
    }

    // =========================================================================
    // 2. CỔNG QUẢN TRỊ VIÊN & NHÂN VIÊN (ADMIN DASHBOARD - /admin)
    // =========================================================================
    @GetMapping({"/admin", "/admin/dashboard", "/analytics"})
    public String adminDashboardPage(Model model) {
        model.addAttribute("initialTab", "dash");
        return "admin/index";
    }

    @GetMapping({"/admin/staff", "/staff"})
    public String adminStaffPosPage(Model model) {
        model.addAttribute("initialTab", "staff");
        return "admin/index";
    }

    @GetMapping({"/admin/courts", "/admin/pricing", "/admin/maintenance"})
    public String adminCourtsPage(Model model) {
        model.addAttribute("initialTab", "admin");
        return "admin/index";
    }

    // =========================================================================
    // 3. TRANG ĐĂNG NHẬP RIÊNG BIỆT (/login)
    // =========================================================================
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("googleClientId", googleClientId);
        return "auth/login";
    }
}
