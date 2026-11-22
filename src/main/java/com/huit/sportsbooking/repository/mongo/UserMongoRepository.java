package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.UserDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByPhone(String phone);
    Optional<UserDocument> findByEmail(String email);
}
