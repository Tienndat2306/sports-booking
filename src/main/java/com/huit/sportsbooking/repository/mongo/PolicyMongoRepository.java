package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.PolicyDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PolicyMongoRepository extends MongoRepository<PolicyDocument, String> {
    Optional<PolicyDocument> findByFacilityIdAndCurrentTrue(String facilityId);
}
