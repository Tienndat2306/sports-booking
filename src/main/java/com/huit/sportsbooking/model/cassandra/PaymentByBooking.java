package com.huit.sportsbooking.model.cassandra;

import java.time.Instant;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Bảng Q4: payments_by_booking (Mục 3.5.2).
 */
@Table("payments_by_booking")
public class PaymentByBooking {

    @PrimaryKeyColumn(name = "booking_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String bookingId;

    @PrimaryKeyColumn(name = "paid_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Instant paidAt;

    @PrimaryKeyColumn(name = "payment_id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private String paymentId;

    @Column("type")
    private String type;            // DEPOSIT | BALANCE | REFUND

    @Column("method")
    private String method;          // CASH | TRANSFER | EWALLET

    @Column("amount_vnd")
    private long amountVnd;

    @Column("status")
    private String status;          // SUCCESS | FAILED | PENDING

    @Column("ref_code")
    private String refCode;

    @Column("staff_id")
    private String staffId;

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public long getAmountVnd() { return amountVnd; }
    public void setAmountVnd(long amountVnd) { this.amountVnd = amountVnd; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRefCode() { return refCode; }
    public void setRefCode(String refCode) { this.refCode = refCode; }
    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
}
