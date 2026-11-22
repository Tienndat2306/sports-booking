package com.huit.sportsbooking.dto.facility;

import java.util.List;

public record FacilityDetailResponse(
        FacilityResponse facility,
        List<CourtResponse> courts
) {}
