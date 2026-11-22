package com.huit.sportsbooking.model.enums;

/**
 * Vòng đời trạng thái của một đơn đặt sân (mục 3.4.3).
 */
public enum BookingStatus {
    HELD,
    CONFIRMED,
    CHECKED_IN,
    COMPLETED,
    CANCELED,
    NO_SHOW,
    EXPIRED
}
