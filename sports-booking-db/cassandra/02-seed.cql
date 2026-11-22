-- Dữ liệu mẫu cho Cassandra (khớp với dữ liệu mẫu MongoDB 02-seed.js)
USE sports_booking;

-- 1. Khung giờ đã bị chiếm (Q1: slot_state_by_court_day)
-- Đơn 01JB0001 chiếm 2 khung 18:00 và 19:00 ngày 2026-11-20 tại sân FAC01-FB-01
INSERT INTO slot_state_by_court_day (court_id, day, slot_start, status, booking_id, user_id, updated_at)
VALUES ('FAC01-FB-01', '2026-11-20', '18:00:00', 'BOOKED', '01JB0001', '01JU0001', '2026-11-16T09:03:00Z');

INSERT INTO slot_state_by_court_day (court_id, day, slot_start, status, booking_id, user_id, updated_at)
VALUES ('FAC01-FB-01', '2026-11-20', '19:00:00', 'BOOKED', '01JB0001', '01JU0001', '2026-11-16T09:03:00Z');

-- Bảo trì 01JM0001 khóa 2 khung 08:00 và 09:00 ngày 2026-11-22 tại sân FAC01-BM-02
INSERT INTO slot_state_by_court_day (court_id, day, slot_start, status, maintenance_id, updated_at)
VALUES ('FAC01-BM-02', '2026-11-22', '08:00:00', 'MAINTENANCE', '01JM0001', '2026-11-16T08:00:00Z');

INSERT INTO slot_state_by_court_day (court_id, day, slot_start, status, maintenance_id, updated_at)
VALUES ('FAC01-BM-02', '2026-11-22', '09:00:00', 'MAINTENANCE', '01JM0001', '2026-11-16T08:00:00Z');

-- 2. Lịch sử đặt của khách (Q2: booking_history_by_user)
INSERT INTO booking_history_by_user (user_id, start_at, booking_id, facility_id, court_id, sport_code, slot_count, total_vnd, status)
VALUES ('01JU0001', '2026-11-20T11:00:00Z', '01JB0001', 'FAC01', 'FAC01-FB-01', 'FOOTBALL5', 2, 700000, 'CONFIRMED');

-- 3. Sự kiện chuyển trạng thái của đơn (Q3: booking_events_by_booking)
INSERT INTO booking_events_by_booking (booking_id, event_time, event_type, actor_id, from_status, to_status, detail)
VALUES ('01JB0001', 13814000-1dd2-11b2-8080-808080808080, 'CONFIRMED', '01JU0001', 'HELD', 'CONFIRMED', {'depositVnd': '210000', 'method': 'EWALLET'});

-- 4. Thanh toán theo đơn (Q4: payments_by_booking)
INSERT INTO payments_by_booking (booking_id, paid_at, payment_id, type, method, amount_vnd, status, ref_code, staff_id)
VALUES ('01JB0001', '2026-11-16T09:03:00Z', '01JP0001', 'DEPOSIT', 'EWALLET', 210000, 'SUCCESS', 'MOMO-20261116-001', null);

-- 5. Thanh toán theo cơ sở và ngày (Q5: payments_by_facility_day)
INSERT INTO payments_by_facility_day (facility_id, day, paid_at, payment_id, booking_id, type, method, amount_vnd, status)
VALUES ('FAC01', '2026-11-16', '2026-11-16T09:03:00Z', '01JP0001', '01JB0001', 'DEPOSIT', 'EWALLET', 210000, 'SUCCESS');

-- 6. Nhật ký hoạt động mẫu (Q8 & Q9: audit_by_day_type & audit_by_user)
INSERT INTO audit_by_day_type (day, event_type, event_time, user_id, role, entity_type, entity_id, detail, ip)
VALUES ('2026-11-16', 'BOOKING_CREATE', 13814000-1dd2-11b2-8080-808080808080, '01JU0001', 'CUSTOMER', 'BOOKING', '01JB0001', 'Đặt sân FAC01-FB-01 ngày 2026-11-20 (18:00-20:00)', '127.0.0.1');

INSERT INTO audit_by_user (user_id, month, event_time, event_type, entity_type, entity_id, detail, ip)
VALUES ('01JU0001', '2026-11', 13814000-1dd2-11b2-8080-808080808080, 'BOOKING_CREATE', 'BOOKING', '01JB0001', 'Đặt sân FAC01-FB-01 ngày 2026-11-20 (18:00-20:00)', '127.0.0.1');
