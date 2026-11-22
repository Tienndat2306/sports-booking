package com.huit.sportsbooking.repository.mongo;

import com.huit.sportsbooking.model.mongo.BookingDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingMongoRepository extends MongoRepository<BookingDocument, String> {
    Optional<BookingDocument> findByCode(String code);
    List<BookingDocument> findByUserIdOrderByCreatedAtDesc(String userId);
    List<BookingDocument> findByFacilityIdAndBookingDateAndStatus(String facilityId, String bookingDate, String status);
    List<BookingDocument> findByCourtIdAndBookingDate(String courtId, String bookingDate);
}
