package model;

import java.time.LocalDate;

public class Reservation {
    private String reservationID;
    private String studentID;
    private String studentName;
    private String equipmentID;
    private String equipmentName;
    private LocalDate reservedDate;
    private String status; // ACTIVE, CANCELLED, FULFILLED

    public Reservation(String studentID, String studentName, String equipmentID, String equipmentName, LocalDate reservedDate) {
        this.reservationID  = "RES-" + System.currentTimeMillis();
        this.studentID      = studentID;
        this.studentName    = studentName;
        this.equipmentID    = equipmentID;
        this.equipmentName  = equipmentName;
        this.reservedDate   = reservedDate;
        this.status         = "ACTIVE";
    }

    public void cancel()  { status = "CANCELLED"; }
    public void fulfill() { status = "FULFILLED"; }

    public String getReservationID() { return reservationID; }
    public String getStudentID()     { return studentID; }
    public String getStudentName()   { return studentName; }
    public String getEquipmentID()   { return equipmentID; }
    public String getEquipmentName() { return equipmentName; }
    public LocalDate getReservedDate() { return reservedDate; }
    public String getStatus()        { return status; }
}
