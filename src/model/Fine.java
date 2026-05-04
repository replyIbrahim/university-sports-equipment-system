package model;

import java.time.LocalDateTime;

public class Fine {
    private String fineID;
    private String borrowRecordID;
    private String borrowerID;
    private double amount;
    private String reason; // LATE_RETURN, DAMAGE
    private boolean isPaid;
    private LocalDateTime createdAt;

    public Fine(String borrowRecordID, String borrowerID, double amount, String reason) {
        this.fineID         = "FN-" + System.currentTimeMillis();
        this.borrowRecordID = borrowRecordID;
        this.borrowerID     = borrowerID;
        this.amount         = amount;
        this.reason         = reason;
        this.isPaid         = false;
        this.createdAt      = LocalDateTime.now();
    }

    public void markPaid() { isPaid = true; }

    public String getFineID()         { return fineID; }
    public String getBorrowRecordID() { return borrowRecordID; }
    public String getBorrowerID()     { return borrowerID; }
    public double getAmount()         { return amount; }
    public String getReason()         { return reason; }
    public boolean isPaid()           { return isPaid; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
