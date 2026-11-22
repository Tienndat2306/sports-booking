package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.PriceRuleDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PriceRuleMongoRepository extends MongoRepository<PriceRuleDocument, String> {
    List<PriceRuleDocument> findByFacilityIdAndSportCodeAndDayType(String facilityId, String sportCode, String dayType);
}
