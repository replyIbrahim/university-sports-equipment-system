package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class BorrowRecord {
    private String recordID;
    private String borrowerID;
    private String borrowerName;
    private String equipmentID;
    private String equipmentName;
    private int quantity;
    private LocalDate borrowDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private String status; // PENDING, APPROVED, REJECTED, RETURNED, OVERDUE
    private String remarks;
    private double fineAmount;

    public BorrowRecord(String borrowerID, String borrowerName, String equipmentID, String equipmentName,
                        int quantity, LocalDate borrowDate, LocalDate expectedReturnDate) {
        this.recordID           = "BR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.borrowerID         = borrowerID;
        this.borrowerName       = borrowerName;
        this.equipmentID        = equipmentID;
        this.equipmentName      = equipmentName;
        this.quantity           = quantity;
        this.borrowDate         = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status             = "PENDING";
        this.remarks            = "";
        this.fineAmount         = 0.0;
    }

    public double calculateFine() {
        LocalDate returnDate = (actualReturnDate != null) ? actualReturnDate : LocalDate.now();
        if (returnDate.isAfter(expectedReturnDate)) {
            long days   = ChronoUnit.DAYS.between(expectedReturnDate, returnDate);
            fineAmount  = days * 50.0; // Rs. 50 per day fine
        }
        return fineAmount;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(expectedReturnDate) && !status.equals("RETURNED");
    }

    // Getters
    public String getRecordID()           { return recordID; }
    public String getBorrowerID()         { return borrowerID; }
    public String getBorrowerName()       { return borrowerName; }
    public String getEquipmentID()        { return equipmentID; }
    public String getEquipmentName()      { return equipmentName; }
    public int getQuantity()              { return quantity; }
    public LocalDate getBorrowDate()      { return borrowDate; }
    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public LocalDate getActualReturnDate()   { return actualReturnDate; }
    public String getStatus()             { return status; }
    public String getRemarks()            { return remarks; }
    public double getFineAmount()         { return fineAmount; }

    // Setters
    public void setStatus(String s)               { status = s; }
    public void setRemarks(String r)              { remarks = r; }
    public void setActualReturnDate(LocalDate d)  { actualReturnDate = d; }
    public void setFineAmount(double f)           { fineAmount = f; }

    @Override
    public String toString() {
        return recordID + " | " + borrowerName + " | " + equipmentName + " x" + quantity + " | " + status;
    }
}
