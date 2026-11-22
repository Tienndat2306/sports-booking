-- Lược đồ Cassandra cho đồ án (mục 3.5.4): 1 keyspace, 9 bảng.
-- Chạy lại nhiều lần vẫn an toàn (IF NOT EXISTS).

CREATE KEYSPACE IF NOT EXISTS sports_booking
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
-- Khi chạy nhiều nút: NetworkTopologyStrategy với replication_factor 3

USE sports_booking;

-- Q1: khung giờ đã bị chiếm của một sân trong một ngày; chốt khung bằng LWT
CREATE TABLE IF NOT EXISTS slot_state_by_court_day (
  court_id text, day date, slot_start time,
  status text, booking_id text, maintenance_id text, user_id text, updated_at timestamp,
  PRIMARY KEY ((court_id, day), slot_start)
) WITH CLUSTERING ORDER BY (slot_start ASC);

-- Q2: lịch sử đặt của một khách, mới nhất trước
CREATE TABLE IF NOT EXISTS booking_history_by_user (
  user_id text, start_at timestamp, booking_id text,
  facility_id text, court_id text, sport_code text,
  slot_count int, total_vnd bigint, status text,
  PRIMARY KEY ((user_id), start_at, booking_id)
) WITH CLUSTERING ORDER BY (start_at DESC, booking_id ASC);

-- Q3: các sự kiện (chuyển trạng thái) của một đơn
CREATE TABLE IF NOT EXISTS booking_events_by_booking (
  booking_id text, event_time timeuuid, event_type text, actor_id text,
  from_status text, to_status text, detail map<text, text>,
  PRIMARY KEY ((booking_id), event_time)
) WITH CLUSTERING ORDER BY (event_time ASC);

-- Q4: thanh toán của một đơn
CREATE TABLE IF NOT EXISTS payments_by_booking (
  booking_id text, paid_at timestamp, payment_id text,
  type text, method text, amount_vnd bigint, status text, ref_code text, staff_id text,
  PRIMARY KEY ((booking_id), paid_at, payment_id)
);

-- Q5: thanh toán của một cơ sở trong một ngày
CREATE TABLE IF NOT EXISTS payments_by_facility_day (
  facility_id text, day date, paid_at timestamp, payment_id text,
  booking_id text, type text, method text, amount_vnd bigint, status text,
  PRIMARY KEY ((facility_id, day), paid_at, payment_id)
);

-- Q6: doanh thu theo ngày, môn, sân trong một tháng (counter)
CREATE TABLE IF NOT EXISTS revenue_daily (
  facility_id text, month text, day date, sport_code text, court_id text,
  revenue_vnd counter, refund_vnd counter, booking_count counter, booked_slots counter,
  PRIMARY KEY ((facility_id, month), day, sport_code, court_id)
);

-- Q7: số khung đã đặt theo giờ trong tháng (counter)
CREATE TABLE IF NOT EXISTS usage_by_hour (
  facility_id text, month text, hour int, day date, booked_slots counter,
  PRIMARY KEY ((facility_id, month), hour, day)
);

-- Q8: nhật ký theo ngày và loại thao tác
CREATE TABLE IF NOT EXISTS audit_by_day_type (
  day date, event_type text, event_time timeuuid,
  user_id text, role text, entity_type text, entity_id text, detail text, ip text,
  PRIMARY KEY ((day, event_type), event_time)
) WITH CLUSTERING ORDER BY (event_time DESC);

-- Q9: nhật ký của một người dùng trong tháng
CREATE TABLE IF NOT EXISTS audit_by_user (
  user_id text, month text, event_time timeuuid,
  event_type text, entity_type text, entity_id text, detail text, ip text,
  PRIMARY KEY ((user_id, month), event_time)
) WITH CLUSTERING ORDER BY (event_time DESC);

DESCRIBE TABLES;
