package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Equipment {
    private String equipmentID;
    private String name;
    private String sportType;  // Cricket, Football, Basketball, Badminton, etc.
    private String category;
    private int totalQuantity;
    private int availableQuantity;
    private String condition;  // Good, Fair, Damaged, Under Repair, Retired
    private String status;     // Available, Unavailable, Under Maintenance
    private int repairCount;
    private List<String> conditionLog;

    public Equipment(String name, String sportType, String category, int quantity, String condition) {
        this.equipmentID    = "EQ-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.name           = name;
        this.sportType      = sportType;
        this.category       = category;
        this.totalQuantity  = quantity;
        this.availableQuantity = quantity;
        this.condition      = condition;
        this.status         = "Available";
        this.repairCount    = 0;
        this.conditionLog   = new ArrayList<>();
        this.conditionLog.add("Initial condition: " + condition);
    }

    public void updateDetails(String name, String condition, int totalQty) {
        this.name          = name;
        this.condition     = condition;
        this.totalQuantity = totalQty;
    }

    public void flagMaintenance() {
        this.status    = "Under Maintenance";
        this.repairCount++;
        conditionLog.add("Flagged for maintenance. Repair count: " + repairCount);
        checkRetirement();
    }

    public void completeMaintenance() {
        this.status    = "Available";
        this.condition = "Good";
        conditionLog.add("Maintenance complete. Condition reset to Good.");
    }

    public void checkRetirement() {
        if (repairCount >= 5) {
            this.status    = "Unavailable";
            this.condition = "Retired";
            conditionLog.add("Equipment RETIRED after 5 repairs.");
        }
    }

    public boolean borrowQty(int qty) {
        if (availableQuantity >= qty) {
            availableQuantity -= qty;
            if (availableQuantity == 0) status = "Unavailable";
            return true;
        }
        return false;
    }

    public void returnQty(int qty) {
        availableQuantity = Math.min(totalQuantity, availableQuantity + qty);
        if (availableQuantity > 0 && !status.equals("Under Maintenance") && !condition.equals("Retired")) {
            status = "Available";
        }
    }

    // Getters
    public String getEquipmentID()      { return equipmentID; }
    public String getName()             { return name; }
    public String getSportType()        { return sportType; }
    public String getCategory()         { return category; }
    public int getTotalQuantity()       { return totalQuantity; }
    public int getAvailableQuantity()   { return availableQuantity; }
    public String getCondition()        { return condition; }
    public String getStatus()           { return status; }
    public int getRepairCount()         { return repairCount; }
    public List<String> getConditionLog() { return conditionLog; }

    // Setters
    public void setStatus(String s)         { status = s; }
    public void setCondition(String c)      { condition = c; }
    public void setTotalQuantity(int q)     { totalQuantity = q; }
    public void setAvailableQuantity(int q) { availableQuantity = q; }
    public void setSportType(String st)     { sportType = st; }
    public void setName(String n)           { name = n; }
    public void setCategory(String c)       { category = c; }

    @Override
    public String toString() {
        return "[" + equipmentID + "] " + name + " (" + sportType + ") - " + status + " [" + availableQuantity + "/" + totalQuantity + "]";
    }
}
