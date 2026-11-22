package com.huit.sportsbooking.model.cassandra;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Bảng Q5: payments_by_facility_day (Mục 3.5.2).
 */
@Table("payments_by_facility_day")
public class PaymentByFacilityDay {

    @PrimaryKeyColumn(name = "facility_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String facilityId;

    @PrimaryKeyColumn(name = "day", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private LocalDate day;

    @PrimaryKeyColumn(name = "paid_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private Instant paidAt;

    @PrimaryKeyColumn(name = "payment_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private String paymentId;

    @Column("booking_id")
    private String bookingId;

    @Column("type")
    private String type;

    @Column("method")
    private String method;

    @Column("amount_vnd")
    private long amountVnd;

    @Column("status")
    private String status;

    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public LocalDate getDay() { return day; }
    public void setDay(LocalDate day) { this.day = day; }
    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public long getAmountVnd() { return amountVnd; }
    public void setAmountVnd(long amountVnd) { this.amountVnd = amountVnd; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
