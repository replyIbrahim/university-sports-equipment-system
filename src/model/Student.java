package model;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private List<String> borrowHistory; // list of BorrowRecord IDs
    private double activeFine;

    public Student(String universityID, String password, String name, String contact, String department) {
        super(universityID, password, name, contact, department, "STUDENT");
        this.borrowHistory = new ArrayList<>();
        this.activeFine = 0.0;
    }

    public void addBorrowRecord(String recordID) {
        borrowHistory.add(recordID);
    }

    public void addFine(double amount) {
        activeFine += amount;
    }

    public void payFine(double amount) {
        activeFine = Math.max(0, activeFine - amount);
    }

    public List<String> getBorrowHistory() { return borrowHistory; }
    public double getActiveFine()          { return activeFine; }
    public void setActiveFine(double f)    { activeFine = f; }
}
