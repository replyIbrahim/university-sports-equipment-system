package model;

public class User {
    private String universityID;
    private String password;
    private String name;
    private String contact;
    private String department;
    private String role; // "STUDENT", "SPORTS_HEAD", "ADMIN"
    private boolean isActive;

    public User(String universityID, String password, String name, String contact, String department, String role) {
        this.universityID = universityID;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.department = department;
        this.role = role;
        this.isActive = true;
    }

    public boolean login(String id, String pass) {
        return this.universityID.equals(id) && this.password.equals(pass) && isActive;
    }

    public void updateProfile(String contact, String department) {
        this.contact = contact;
        this.department = department;
    }

    // Getters
    public String getUniversityID() { return universityID; }
    public String getPassword()     { return password; }
    public String getName()         { return name; }
    public String getContact()      { return contact; }
    public String getDepartment()   { return department; }
    public String getRole()         { return role; }
    public boolean isActive()       { return isActive; }

    // Setters
    public void setActive(boolean active) { isActive = active; }
    public void setContact(String c)      { contact = c; }
    public void setDepartment(String d)   { department = d; }

    @Override
    public String toString() {
        return name + " [" + role + "] - " + universityID;
    }
}
