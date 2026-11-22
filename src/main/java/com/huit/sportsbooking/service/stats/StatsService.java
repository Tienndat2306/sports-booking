package com.huit.sportsbooking.service.stats;

/**
 * Service thống kê & kiểm toán (Module 4 - UC20, UC22, UC26).
 * Thành viên phụ trách Module 4 sẽ xử lý:
 * - Cassandra Counters: revenue_daily, usage_by_hour...
 * - Cassandra Audit: audit_by_day_type, audit_by_user...
 * - Redis Stream Consumer: sb:stream:events (nhóm cg-stats)
 */
public interface StatsService {
    // Các phương thức: getDailyRevenue, getCourtUtilization, getAuditLogs...
}
