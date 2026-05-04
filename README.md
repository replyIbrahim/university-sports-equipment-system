# 🏆 University Sports Equipment Management System

**Course:** Software Design & Analysis  
**Submitted By:**
- Ibrahim Malik (22P-4953)
- Ibbad-ur-Rehman (24P-0624)
- Muhammad Ayyan (24P-0535)

---

## 📌 About

A fully working Java Swing GUI application implementing Scenario 7: Sports Equipment Management System.

---

## 🚀 How to Compile & Run

### Requirements
- Java JDK 11 or higher

### Step 1 — Clone
```bash
git clone <your-repo-url>
cd SportsEquipmentMS
```

### Step 2 — Compile
```bash
mkdir out
javac -sourcepath src -d out src/Main.java
```

### Step 3 — Run
```bash
java -cp out Main
```

---

## 🔐 Demo Login Credentials

| Role        | University ID | Password |
|-------------|--------------|----------|
| Sports Head | SH001        | head123  |
| Student     | 22P-4953     | pass123  |
| Student     | 24P-0624     | pass123  |
| Student     | 24P-0535     | pass123  |

---

## ✅ Features Implemented

### Use Cases Covered (from SRS)
1. **Login** — Role-based routing (Student / Sports Head)
2. **Browse Equipment** — Filter by sport type, real-time availability
3. **Submit Borrow Request** — Quantity validation, availability check
4. **Approve / Reject Requests** — Sports Head with remarks
5. **Return Equipment** — Auto fine calculation (Rs. 50/day)
6. **Reserve Equipment** — Future reservation system
7. **Submit Feedback / Complaint** — With type selection
8. **Add / Remove Equipment** — Auto-generated Equipment ID
9. **Flag / Complete Maintenance** — Auto-retirement after 5 repairs
10. **Manage Users** — Register, activate/deactivate accounts
11. **Inventory Report** — Summary stats by sport type
12. **View Fines** — Student fine tracking + pay

---

## 🏗️ Architecture

```
src/
├── model/          ← Classes from Class Diagram
│   ├── User.java
│   ├── Student.java      (extends User)
│   ├── SportsHead.java   (extends User)
│   ├── Equipment.java
│   ├── BorrowRecord.java
│   ├── Fine.java
│   ├── Feedback.java
│   └── Reservation.java
├── data/
│   └── DataStore.java    ← Singleton pattern
├── gui/
│   ├── UITheme.java      ← Shared styling
│   ├── LoginFrame.java
│   ├── StudentDashboard.java
│   └── SportsHeadDashboard.java
└── Main.java
```

---

## 🧩 Design Patterns Used

| Pattern     | Where                      | Why                                              |
|-------------|----------------------------|--------------------------------------------------|
| **Singleton** | `DataStore.java`         | One shared data source across all GUI panels     |
| **Inheritance** | `Student`, `SportsHead` extend `User` | Reuse login/profile; add role-specific ops |
| **Factory (manual)** | `LoginFrame.doLogin()` | Creates correct dashboard based on role       |
| **Strategy** | `BorrowRecord.calculateFine()` | Fine logic encapsulated per record           |

---

## 📊 Class Diagram → Code Mapping

| SRS Class      | Java Class         | Notes                              |
|----------------|--------------------|------------------------------------|
| User           | model/User.java    | universityID, password, role, active |
| Student        | model/Student.java | extends User, borrowHistory        |
| SportsHead     | model/SportsHead.java | extends User, approve/reject    |
| Equipment      | model/Equipment.java | auto ID, status, repairCount     |
| BorrowRecord   | model/BorrowRecord.java | calculateFine(), isOverdue()  |
| Fine           | model/Fine.java    | markPaid()                         |
| Feedback       | model/Feedback.java | FEEDBACK / COMPLAINT types        |
| Reservation    | model/Reservation.java | Active/Cancelled/Fulfilled     |

---

## ⚠️ Academic Note

All code was written with LLM assistance (Claude). Every line has been reviewed, understood, and can be explained verbally by team members.
