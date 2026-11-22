package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.MaintenanceDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MaintenanceMongoRepository extends MongoRepository<MaintenanceDocument, String> {
    List<MaintenanceDocument> findByFacilityIdAndStatus(String facilityId, String status);
}
