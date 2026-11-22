package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.ReviewDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewMongoRepository extends MongoRepository<ReviewDocument, String> {
    List<ReviewDocument> findByCourtIdOrderByCreatedAtDesc(String courtId);
}
