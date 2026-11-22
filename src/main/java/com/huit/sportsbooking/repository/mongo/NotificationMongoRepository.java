package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.NotificationDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationMongoRepository extends MongoRepository<NotificationDocument, String> {
    List<NotificationDocument> findByUserIdOrderByCreatedAtDesc(String userId);
}
