package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.CourtDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourtMongoRepository extends MongoRepository<CourtDocument, String> {
    List<CourtDocument> findByFacilityIdAndStatus(String facilityId, String status);
    List<CourtDocument> findByFacilityIdAndSportCodeAndStatus(String facilityId, String sportCode, String status);
}
