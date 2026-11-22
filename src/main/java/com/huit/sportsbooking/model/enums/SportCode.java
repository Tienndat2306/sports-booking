package com.huit.sportsbooking.model.enums;

/**
 * Mã môn thể thao chuẩn (mục 3.1.1).
 */
public enum SportCode {
    FOOTBALL5("Bóng đá 5 người"),
    BADMINTON("Cầu lông"),
    VOLLEYBALL("Bóng chuyền");

    private final String displayName;

    SportCode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
