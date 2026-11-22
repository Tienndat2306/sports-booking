package com.huit.sportsbooking.repository.cassandra;

import com.huit.sportsbooking.model.cassandra.SlotStateByCourtDay;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Repository;

/**
 * Truy xuất và chốt khung giờ có điều kiện (LWT - Lightweight Transaction) trên Cassandra (Mục 3.5.4 & 3.8.1).
 */
@Repository
public class SlotStateCassandraDao {

    private final CassandraOperations cassandraOperations;
    private final CqlTemplate cqlTemplate;

    public SlotStateCassandraDao(CassandraOperations cassandraOperations, CqlTemplate cqlTemplate) {
        this.cassandraOperations = cassandraOperations;
        this.cqlTemplate = cqlTemplate;
    }

    /**
     * Lấy danh sách các khung giờ đã bị chiếm (BOOKED hoặc MAINTENANCE) của 1 sân trong 1 ngày.
     */
    public List<SlotStateByCourtDay> findOccupiedSlots(String courtId, LocalDate day) {
        String cql = "SELECT * FROM slot_state_by_court_day WHERE court_id = ? AND day = ?";
        return cassandraOperations.select(
                com.datastax.oss.driver.api.core.cql.SimpleStatement.newInstance(cql, courtId, day),
                SlotStateByCourtDay.class
        );
    }

    /**
     * Chốt 1 khung giờ bằng LWT (IF NOT EXISTS).
     * Trả về true nếu chốt thành công, false nếu khung giờ đã bị người khác đặt hoặc bảo trì.
     */
    public boolean tryClaimSlot(String courtId, LocalDate day, LocalTime slotStart,
                                String bookingId, String userId) {
        String cql = """
                INSERT INTO slot_state_by_court_day
                (court_id, day, slot_start, status, booking_id, user_id, updated_at)
                VALUES (?, ?, ?, 'BOOKED', ?, ?, ?)
                IF NOT EXISTS
                """;
        Boolean applied = cqlTemplate.queryForObject(
                cql, Boolean.class,
                courtId, day, slotStart, bookingId, userId, Instant.now()
        );
        return Boolean.TRUE.equals(applied);
    }

    /**
     * Trả khung giờ có điều kiện khi hủy đơn hoặc không đến (IF booking_id = ?).
     */
    public boolean releaseBookedSlot(String courtId, LocalDate day, LocalTime slotStart, String bookingId) {
        String cql = """
                DELETE FROM slot_state_by_court_day
                WHERE court_id = ? AND day = ? AND slot_start = ?
                IF booking_id = ?
                """;
        Boolean applied = cqlTemplate.queryForObject(
                cql, Boolean.class,
                courtId, day, slotStart, bookingId
        );
        return Boolean.TRUE.equals(applied);
    }
}
