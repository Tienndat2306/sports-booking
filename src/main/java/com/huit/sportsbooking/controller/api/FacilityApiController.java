package com.huit.sportsbooking.controller.api;

import com.huit.sportsbooking.common.ApiResponse;
import com.huit.sportsbooking.dto.facility.FacilityDetailResponse;
import com.huit.sportsbooking.dto.facility.FacilityResponse;
import com.huit.sportsbooking.service.catalog.FacilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API phục vụ tìm kiếm và xem chi tiết cơ sở, sân thể thao (Module 1 - UC05, UC08).
 * Đây là Controller API mẫu chuẩn cho toàn bộ dự án.
 */
@RestController
@RequestMapping("/api/v1/facilities")
public class FacilityApiController {

    private final FacilityService facilityService;

    public FacilityApiController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    /**
     * Lấy danh sách các cơ sở thể thao đang hoạt động.
     * GET /api/v1/facilities
     */
    @GetMapping
    public ApiResponse<List<FacilityResponse>> getAllFacilities() {
        List<FacilityResponse> result = facilityService.getAllActiveFacilities();
        return ApiResponse.ok("Lấy danh sách cơ sở thành công", result);
    }

    /**
     * Lấy chi tiết một cơ sở kèm danh sách các sân.
     * GET /api/v1/facilities/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<FacilityDetailResponse> getFacilityDetail(@PathVariable("id") String id) {
        FacilityDetailResponse result = facilityService.getFacilityDetail(id);
        return ApiResponse.ok("Lấy chi tiết cơ sở thành công", result);
    }
}
