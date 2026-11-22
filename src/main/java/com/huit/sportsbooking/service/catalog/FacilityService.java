package com.huit.sportsbooking.service.catalog;

import com.huit.sportsbooking.common.AppException;
import com.huit.sportsbooking.common.ErrorCode;
import com.huit.sportsbooking.dto.facility.CourtResponse;
import com.huit.sportsbooking.dto.facility.FacilityDetailResponse;
import com.huit.sportsbooking.dto.facility.FacilityResponse;
import com.huit.sportsbooking.model.mongo.CourtDocument;
import com.huit.sportsbooking.model.mongo.FacilityDocument;
import com.huit.sportsbooking.repository.mongo.CourtMongoRepository;
import com.huit.sportsbooking.repository.mongo.FacilityMongoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service xử lý nghiệp vụ Danh mục Cơ sở & Sân (Module 1 - UC05, UC08).
 * Đây là Service mẫu chuẩn để các thành viên tham khảo cách viết Service cho các module khác.
 */
@Service
public class FacilityService {

    private final FacilityMongoRepository facilityRepo;
    private final CourtMongoRepository courtRepo;

    public FacilityService(FacilityMongoRepository facilityRepo, CourtMongoRepository courtRepo) {
        this.facilityRepo = facilityRepo;
        this.courtRepo = courtRepo;
    }

    /**
     * Lấy danh sách tất cả cơ sở đang hoạt động (ACTIVE).
     */
    public List<FacilityResponse> getAllActiveFacilities() {
        List<FacilityDocument> facilities = facilityRepo.findByStatus("ACTIVE");
        return facilities.stream()
                .map(this::toFacilityResponse)
                .toList();
    }

    /**
     * Lấy thông tin chi tiết một cơ sở kèm danh sách các sân (Courts).
     */
    public FacilityDetailResponse getFacilityDetail(String facilityId) {
        FacilityDocument facility = facilityRepo.findById(facilityId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy cơ sở với mã: " + facilityId));

        List<CourtDocument> courts = courtRepo.findByFacilityIdAndStatus(facilityId, "ACTIVE");
        List<CourtResponse> courtResponses = courts.stream()
                .map(this::toCourtResponse)
                .toList();

        return new FacilityDetailResponse(toFacilityResponse(facility), courtResponses);
    }

    /**
     * Chuyển đổi Entity FacilityDocument sang DTO FacilityResponse.
     */
    private FacilityResponse toFacilityResponse(FacilityDocument doc) {
        String fullAddress = "";
        String district = "";
        String city = "";
        if (doc.getAddress() != null) {
            district = doc.getAddress().getDistrict() != null ? doc.getAddress().getDistrict() : "";
            city = doc.getAddress().getCity() != null ? doc.getAddress().getCity() : "";
            fullAddress = String.format("%s, %s, %s",
                    doc.getAddress().getStreet() != null ? doc.getAddress().getStreet() : "",
                    district, city).replaceAll("^[ ,]+|[ ,]+$", "");
        }

        double lng = 0.0;
        double lat = 0.0;
        if (doc.getLocation() != null) {
            lng = doc.getLocation().getX();
            lat = doc.getLocation().getY();
        }

        String openHours = "";
        if (doc.getHours() != null) {
            openHours = String.format("%s - %s", doc.getHours().getOpen(), doc.getHours().getClose());
        }

        return new FacilityResponse(
                doc.getId(),
                doc.getName(),
                fullAddress,
                district,
                city,
                lng,
                lat,
                openHours,
                doc.getSports() != null ? doc.getSports() : List.of(),
                doc.getStatus()
        );
    }

    /**
     * Chuyển đổi Entity CourtDocument sang DTO CourtResponse.
     */
    private CourtResponse toCourtResponse(CourtDocument doc) {
        return new CourtResponse(
                doc.getId(),
                doc.getFacilityId(),
                doc.getSportCode(),
                doc.getName(),
                doc.getStatus(),
                doc.getMaxPlayers(),
                doc.getAttributes() != null ? doc.getAttributes() : Map.of(),
                doc.getAmenities() != null ? doc.getAmenities() : List.of()
        );
    }
}
