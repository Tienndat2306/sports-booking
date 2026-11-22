package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.CategoryDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryMongoRepository extends MongoRepository<CategoryDocument, String> {
    List<CategoryDocument> findByTypeAndActiveTrue(String type);
}
