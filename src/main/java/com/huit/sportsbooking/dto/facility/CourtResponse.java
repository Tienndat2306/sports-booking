package com.huit.sportsbooking.dto.facility;

import java.util.List;
import java.util.Map;

public record CourtResponse(
        String id,
        String facilityId,
        String sportCode,
        String name,
        String status,
        int maxPlayers,
        Map<String, Object> attributes,
        List<String> amenities
) {}
