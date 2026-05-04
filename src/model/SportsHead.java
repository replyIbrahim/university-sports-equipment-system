package model;

public class SportsHead extends User {

    public SportsHead(String universityID, String password, String name, String contact, String department) {
        super(universityID, password, name, contact, department, "SPORTS_HEAD");
    }

    public void registerUser(User u) {
        // logic handled in DataStore
        System.out.println("Registering user: " + u.getName());
    }

    public void approveRequest(BorrowRecord record) {
        record.setStatus("APPROVED");
    }

    public void rejectRequest(BorrowRecord record, String remarks) {
        record.setStatus("REJECTED");
        record.setRemarks(remarks);
    }
}
