package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.FacilityDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FacilityMongoRepository extends MongoRepository<FacilityDocument, String> {
    List<FacilityDocument> findByStatus(String status);
}
