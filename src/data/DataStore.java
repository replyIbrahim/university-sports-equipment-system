package data;

import model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// PATTERN: Singleton — ensures one shared data source across the whole app
public class DataStore {

    private static DataStore instance;

    public List<User>         users         = new ArrayList<>();
    public List<Equipment>    equipment     = new ArrayList<>();
    public List<BorrowRecord> borrowRecords = new ArrayList<>();
    public List<Fine>         fines         = new ArrayList<>();
    public List<Feedback>     feedbacks     = new ArrayList<>();
    public List<Reservation>  reservations  = new ArrayList<>();

    private DataStore() {
        seedData();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // ─── Seed Sample Data ────────────────────────────────────────────────────

    private void seedData() {
        // Sports Head
        users.add(new SportsHead("SH001", "head123", "Dr. Ahmed Khan", "0300-1234567", "Sports Department"));

        // Students
        users.add(new Student("22P-4953", "pass123", "Ibrahim Malik",     "0311-1111111", "CS"));
        users.add(new Student("24P-0624", "pass123", "Ibbad-ur-Rehman",   "0322-2222222", "SE"));
        users.add(new Student("24P-0535", "pass123", "Muhammad Ayyan",    "0333-3333333", "CS"));
        users.add(new Student("24P-0001", "pass123", "Ali Hassan",        "0344-4444444", "EE"));
        users.add(new Student("24P-0002", "pass123", "Sara Ahmed",        "0355-5555555", "BBA"));

        // Equipment
        equipment.add(new Equipment("Cricket Bat",          "Cricket",    "Batting",   10, "Good"));
        equipment.add(new Equipment("Cricket Ball",         "Cricket",    "Bowling",   20, "Good"));
        equipment.add(new Equipment("Cricket Gloves",       "Cricket",    "Protection", 8, "Fair"));
        equipment.add(new Equipment("Football",             "Football",   "Ball",      15, "Good"));
        equipment.add(new Equipment("Football Boots",       "Football",   "Footwear",   6, "Good"));
        equipment.add(new Equipment("Basketball",           "Basketball", "Ball",      10, "Good"));
        equipment.add(new Equipment("Basketball Jersey",    "Basketball", "Clothing",  12, "Good"));
        equipment.add(new Equipment("Badminton Racket",     "Badminton",  "Racket",    16, "Good"));
        equipment.add(new Equipment("Badminton Shuttle",    "Badminton",  "Shuttle",   30, "Fair"));
        equipment.add(new Equipment("Table Tennis Bat",     "Table Tennis","Bat",      12, "Good"));
        equipment.add(new Equipment("Volleyball",           "Volleyball", "Ball",       8, "Good"));
        equipment.add(new Equipment("Hockey Stick",         "Hockey",     "Stick",     10, "Good"));

        // One equipment under maintenance
        equipment.get(2).flagMaintenance();

        // Sample borrow record (PENDING)
        BorrowRecord br = new BorrowRecord(
            "24P-0001", "Ali Hassan",
            equipment.get(0).getEquipmentID(), "Cricket Bat",
            2, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5)
        );
        borrowRecords.add(br);

        // Sample approved + overdue record with fine
        BorrowRecord overdueBr = new BorrowRecord(
            "24P-0002", "Sara Ahmed",
            equipment.get(3).getEquipmentID(), "Football",
            1, LocalDate.now().minusDays(10), LocalDate.now().minusDays(3)
        );
        overdueBr.setStatus("APPROVED");
        equipment.get(3).borrowQty(1);
        borrowRecords.add(overdueBr);

        double fine = overdueBr.calculateFine();
        if (fine > 0) {
            fines.add(new Fine(overdueBr.getRecordID(), overdueBr.getBorrowerID(), fine, "LATE_RETURN"));
        }

        // Sample reservation
        reservations.add(new Reservation(
            "24P-4953", "Ibrahim Malik",
            equipment.get(7).getEquipmentID(), "Badminton Racket",
            LocalDate.now().plusDays(2)
        ));
    }

    // ─── Helper Methods ───────────────────────────────────────────────────────

    public User findUserByID(String id) {
        return users.stream().filter(u -> u.getUniversityID().equals(id)).findFirst().orElse(null);
    }

    public Equipment findEquipmentByID(String id) {
        return equipment.stream().filter(e -> e.getEquipmentID().equals(id)).findFirst().orElse(null);
    }

    public List<BorrowRecord> getBorrowRecordsForUser(String userID) {
        return borrowRecords.stream()
            .filter(br -> br.getBorrowerID().equals(userID))
            .collect(Collectors.toList());
    }

    public List<Fine> getFinesForUser(String userID) {
        return fines.stream()
            .filter(f -> f.getBorrowerID().equals(userID))
            .collect(Collectors.toList());
    }

    public List<BorrowRecord> getPendingRequests() {
        return borrowRecords.stream()
            .filter(br -> br.getStatus().equals("PENDING"))
            .collect(Collectors.toList());
    }

    public List<Equipment> getEquipmentBySport(String sport) {
        if (sport.equals("All")) return new ArrayList<>(equipment);
        return equipment.stream()
            .filter(e -> e.getSportType().equalsIgnoreCase(sport))
            .collect(Collectors.toList());
    }

    public boolean userHasActiveBorrow(String userID) {
        return borrowRecords.stream().anyMatch(
            br -> br.getBorrowerID().equals(userID) && br.getStatus().equals("APPROVED")
        );
    }
}
