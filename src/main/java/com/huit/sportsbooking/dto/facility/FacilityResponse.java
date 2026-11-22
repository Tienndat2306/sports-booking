package com.huit.sportsbooking.dto.facility;

import java.util.List;

public record FacilityResponse(
        String id,
        String name,
        String fullAddress,
        String district,
        String city,
        double longitude,
        double latitude,
        String openHours,
        List<String> sports,
        String status
) {}
