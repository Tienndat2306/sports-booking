package com.huit.sportsbooking.service.booking;

/**
 * Service lõi quản lý đặt sân & thanh toán (Module 2 - UC09-UC15).
 * Thành viên phụ trách Module 2 sẽ sử dụng:
 * - RedisLuaExecutor.holdSlots(...) để giữ chỗ 5 phút.
 * - SlotStateCassandraDao.tryClaimSlot(...) để chốt khung giờ bằng LWT (IF NOT EXISTS).
 * - BookingMongoRepository để lưu đơn đặt cọc.
 */
public interface BookingService {
    // Các phương thức: holdSlots, confirmBooking, cancelBooking, getCourtSchedule...
}
