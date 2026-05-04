package gui;

import data.DataStore;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final Student   student;
    private final DataStore ds = DataStore.getInstance();
    private JTabbedPane     tabs;

    // Equipment tab
    private DefaultTableModel equipModel;
    private JComboBox<String> sportFilter;

    // Borrow tab
    private JComboBox<String> borrowEquipCombo;
    private JSpinner          qtySpinner;
    private JSpinner          returnDateSpinner;
    private JLabel            borrowStatusLabel;

    // My Records tab
    private DefaultTableModel recordsModel;

    // Fines tab
    private DefaultTableModel finesModel;

    // Reserve tab
    private JComboBox<String> resEquipCombo;
    private JSpinner          resDateSpinner;
    private JLabel            resStatus;

    public StudentDashboard(Student student) {
        this.student = student;
        setTitle("Student Dashboard — " + student.getName());
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);

        setVisible(true);
    }

    // ─── Header ──────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
            new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel logo  = UITheme.makeLabel("🏆  Sports Equipment Management", UITheme.FONT_HEAD, UITheme.TEXT_PRIMARY);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JLabel user = UITheme.makeLabel("👤  " + student.getName() + "  [" + student.getUniversityID() + "]",
                UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        JButton logout = UITheme.makeButton("Logout", UITheme.ACCENT_RED, Color.WHITE);
        logout.addActionListener(e -> { dispose(); new LoginFrame(); });

        right.add(user);
        right.add(logout);
        p.add(logo,  BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ─── Tabs ─────────────────────────────────────────────────────────────────

    private JTabbedPane buildTabs() {
        tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);

        tabs.addTab("📦  Equipment",    buildEquipmentTab());
        tabs.addTab("📋  Borrow",       buildBorrowTab());
        tabs.addTab("📁  My Records",   buildRecordsTab());
        tabs.addTab("💰  Fines",        buildFinesTab());
        tabs.addTab("📅  Reserve",      buildReserveTab());
        tabs.addTab("💬  Feedback",     buildFeedbackTab());
        return tabs;
    }

    // ─── Tab 1: Browse Equipment ──────────────────────────────────────────────

    private JPanel buildEquipmentTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);
        filterBar.add(UITheme.makeLabel("Filter by Sport:", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        sportFilter = UITheme.makeCombo(new String[]{"All","Cricket","Football","Basketball","Badminton","Table Tennis","Volleyball","Hockey"});
        sportFilter.addActionListener(e -> refreshEquipmentTable());
        filterBar.add(sportFilter);

        JButton refreshBtn = UITheme.makeButton("⟳ Refresh", UITheme.ACCENT_BLUE, Color.WHITE);
        refreshBtn.addActionListener(e -> refreshEquipmentTable());
        filterBar.add(refreshBtn);

        // Table
        equipModel = new DefaultTableModel(
            new String[]{"ID","Name","Sport","Category","Available","Total","Condition","Status"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        JTable table = new JTable(equipModel);
        UITheme.styleTable(table);

        // Color-code status column
        table.getColumnModel().getColumn(7).setCellRenderer((tbl, value, selected, focused, row, col) -> {
            JLabel lbl = new JLabel(value == null ? "" : value.toString());
            lbl.setOpaque(true);
            lbl.setFont(UITheme.FONT_SMALL);
            String v = value == null ? "" : value.toString();
            lbl.setBackground(selected ? UITheme.ACCENT_BLUE.darker() : UITheme.BG_CARD);
            lbl.setForeground(v.equals("Available") ? UITheme.ACCENT_GREEN
                    : v.equals("Unavailable")        ? UITheme.ACCENT_RED
                    : UITheme.ACCENT_ORANGE);
            lbl.setBorder(new EmptyBorder(0, 8, 0, 8));
            return lbl;
        });

        refreshEquipmentTable();

        p.add(filterBar,               BorderLayout.NORTH);
        p.add(UITheme.makeScroll(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshEquipmentTable() {
        equipModel.setRowCount(0);
        String sport = (String) sportFilter.getSelectedItem();
        for (Equipment e : ds.getEquipmentBySport(sport)) {
            equipModel.addRow(new Object[]{
                e.getEquipmentID(), e.getName(), e.getSportType(), e.getCategory(),
                e.getAvailableQuantity(), e.getTotalQuantity(), e.getCondition(), e.getStatus()
            });
        }
    }

    // ─── Tab 2: Borrow Request ────────────────────────────────────────────────

    private JPanel buildBorrowTab() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG_DARK);

        JPanel card = UITheme.makeCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(480, 380));

        JLabel title = UITheme.makeLabel("Submit Borrow Request", UITheme.FONT_HEAD, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));

        // Equipment combo
        card.add(UITheme.makeLabel("Select Equipment", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(Box.createVerticalStrut(5));
        borrowEquipCombo = UITheme.makeCombo(getEquipmentOptions());
        borrowEquipCombo.setAlignmentX(LEFT_ALIGNMENT);
        borrowEquipCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(borrowEquipCombo);
        card.add(Box.createVerticalStrut(14));

        // Quantity
        card.add(UITheme.makeLabel("Quantity", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(Box.createVerticalStrut(5));
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        styleSpinner(qtySpinner);
        qtySpinner.setAlignmentX(LEFT_ALIGNMENT);
        card.add(qtySpinner);
        card.add(Box.createVerticalStrut(14));

        // Return date (days from today)
        card.add(UITheme.makeLabel("Return in (days)", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(Box.createVerticalStrut(5));
        returnDateSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 30, 1));
        styleSpinner(returnDateSpinner);
        returnDateSpinner.setAlignmentX(LEFT_ALIGNMENT);
        card.add(returnDateSpinner);
        card.add(Box.createVerticalStrut(20));

        JButton submit = UITheme.makeButton("  Submit Request  ", UITheme.ACCENT_GREEN, Color.BLACK);
        submit.setAlignmentX(LEFT_ALIGNMENT);
        submit.addActionListener(e -> submitBorrow());
        card.add(submit);
        card.add(Box.createVerticalStrut(12));

        borrowStatusLabel = UITheme.makeLabel("", UITheme.FONT_SMALL, UITheme.ACCENT_GREEN);
        borrowStatusLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(borrowStatusLabel);

        outer.add(card);
        return outer;
    }

    private String[] getEquipmentOptions() {
        return ds.equipment.stream()
            .filter(e -> e.getStatus().equals("Available") && e.getAvailableQuantity() > 0)
            .map(e -> e.getEquipmentID() + " — " + e.getName() + " (" + e.getAvailableQuantity() + " avail)")
            .toArray(String[]::new);
    }

    private void submitBorrow() {
        if (borrowEquipCombo.getItemCount() == 0) {
            borrowStatusLabel.setForeground(UITheme.ACCENT_RED);
            borrowStatusLabel.setText("⚠ No equipment available.");
            return;
        }
        if (ds.userHasActiveBorrow(student.getUniversityID())) {
            borrowStatusLabel.setForeground(UITheme.ACCENT_RED);
            borrowStatusLabel.setText("⚠ You already have an active borrow. Return it first.");
            return;
        }

        String selected = (String) borrowEquipCombo.getSelectedItem();
        String eqID     = selected.split(" — ")[0];
        Equipment eq    = ds.findEquipmentByID(eqID);
        int qty         = (int) qtySpinner.getValue();
        int days        = (int) returnDateSpinner.getValue();

        if (eq == null) return;
        if (eq.getAvailableQuantity() < qty) {
            borrowStatusLabel.setForeground(UITheme.ACCENT_RED);
            borrowStatusLabel.setText("⚠ Only " + eq.getAvailableQuantity() + " available.");
            return;
        }

        BorrowRecord br = new BorrowRecord(
            student.getUniversityID(), student.getName(),
            eq.getEquipmentID(), eq.getName(),
            qty, LocalDate.now(), LocalDate.now().plusDays(days)
        );
        ds.borrowRecords.add(br);
        student.addBorrowRecord(br.getRecordID());

        borrowStatusLabel.setForeground(UITheme.ACCENT_GREEN);
        borrowStatusLabel.setText("✔ Request submitted! ID: " + br.getRecordID() + "  (Awaiting approval)");

        // Refresh combos
        borrowEquipCombo.removeAllItems();
        for (String s : getEquipmentOptions()) borrowEquipCombo.addItem(s);
    }

    // ─── Tab 3: My Records ────────────────────────────────────────────────────

    private JPanel buildRecordsTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        recordsModel = new DefaultTableModel(
            new String[]{"Record ID","Equipment","Qty","Borrow Date","Return Date","Status","Fine (Rs.)"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        JTable t = new JTable(recordsModel);
        UITheme.styleTable(t);
        refreshRecords();

        JButton refresh = UITheme.makeButton("⟳ Refresh", UITheme.ACCENT_BLUE, Color.WHITE);
        refresh.addActionListener(e -> refreshRecords());

        JButton returnBtn = UITheme.makeButton("↩ Return Selected", UITheme.ACCENT_ORANGE, Color.BLACK);
        returnBtn.addActionListener(e -> returnEquipment(t));

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.add(refresh);
        bar.add(returnBtn);

        p.add(bar,                        BorderLayout.NORTH);
        p.add(UITheme.makeScroll(t),      BorderLayout.CENTER);
        return p;
    }

    private void refreshRecords() {
        recordsModel.setRowCount(0);
        for (BorrowRecord br : ds.getBorrowRecordsForUser(student.getUniversityID())) {
            recordsModel.addRow(new Object[]{
                br.getRecordID(), br.getEquipmentName(), br.getQuantity(),
                br.getBorrowDate(), br.getExpectedReturnDate(),
                br.getStatus(), String.format("%.0f", br.calculateFine())
            });
        }
    }

    private void returnEquipment(JTable t) {
        int row = t.getSelectedRow();
        if (row < 0) { showInfo("Please select a record to return."); return; }
        String rid = (String) recordsModel.getValueAt(row, 0);

        BorrowRecord br = ds.borrowRecords.stream()
            .filter(r -> r.getRecordID().equals(rid)).findFirst().orElse(null);

        if (br == null || !br.getStatus().equals("APPROVED")) {
            showInfo("Only APPROVED records can be returned.");
            return;
        }

        br.setActualReturnDate(LocalDate.now());
        br.setStatus("RETURNED");
        double fine = br.calculateFine();

        Equipment eq = ds.findEquipmentByID(br.getEquipmentID());
        if (eq != null) eq.returnQty(br.getQuantity());

        if (fine > 0) {
            ds.fines.add(new Fine(br.getRecordID(), student.getUniversityID(), fine, "LATE_RETURN"));
            showInfo("Equipment returned.\n⚠ Late fine applied: Rs. " + fine + "\nCheck the Fines tab.");
        } else {
            showInfo("✔ Equipment returned successfully. No fine.");
        }
        refreshRecords();
    }

    // ─── Tab 4: Fines ─────────────────────────────────────────────────────────

    private JPanel buildFinesTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        finesModel = new DefaultTableModel(
            new String[]{"Fine ID","Record ID","Amount (Rs.)","Reason","Paid?"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        JTable t = new JTable(finesModel);
        UITheme.styleTable(t);
        refreshFines();

        JButton refresh = UITheme.makeButton("⟳ Refresh", UITheme.ACCENT_BLUE, Color.WHITE);
        refresh.addActionListener(e -> refreshFines());

        JButton pay = UITheme.makeButton("✔ Mark as Paid", UITheme.ACCENT_GREEN, Color.BLACK);
        pay.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) { showInfo("Select a fine first."); return; }
            String fid = (String) finesModel.getValueAt(row, 0);
            ds.fines.stream().filter(f -> f.getFineID().equals(fid)).findFirst().ifPresent(f -> {
                f.markPaid();
                showInfo("Fine marked as paid.");
                refreshFines();
            });
        });

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.add(refresh);
        bar.add(pay);

        p.add(bar, BorderLayout.NORTH);
        p.add(UITheme.makeScroll(t), BorderLayout.CENTER);
        return p;
    }

    private void refreshFines() {
        finesModel.setRowCount(0);
        for (Fine f : ds.getFinesForUser(student.getUniversityID())) {
            finesModel.addRow(new Object[]{
                f.getFineID(), f.getBorrowRecordID(),
                String.format("%.0f", f.getAmount()), f.getReason(),
                f.isPaid() ? "Yes" : "No"
            });
        }
    }

    // ─── Tab 5: Reserve ──────────────────────────────────────────────────────

    private JPanel buildReserveTab() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG_DARK);

        JPanel card = UITheme.makeCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(460, 300));

        JLabel title = UITheme.makeLabel("Reserve Equipment", UITheme.FONT_HEAD, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(UITheme.makeLabel("Reserve ahead when equipment is currently borrowed.", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(Box.createVerticalStrut(20));

        card.add(UITheme.makeLabel("Select Equipment", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(Box.createVerticalStrut(5));
        resEquipCombo = UITheme.makeCombo(
            ds.equipment.stream().map(e -> e.getEquipmentID() + " — " + e.getName()).toArray(String[]::new)
        );
        resEquipCombo.setAlignmentX(LEFT_ALIGNMENT);
        resEquipCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(resEquipCombo);
        card.add(Box.createVerticalStrut(14));

        card.add(UITheme.makeLabel("Reserve in (days from today)", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(Box.createVerticalStrut(5));
        resDateSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 30, 1));
        styleSpinner(resDateSpinner);
        resDateSpinner.setAlignmentX(LEFT_ALIGNMENT);
        card.add(resDateSpinner);
        card.add(Box.createVerticalStrut(20));

        JButton submit = UITheme.makeButton("  Reserve  ", UITheme.ACCENT_ORANGE, Color.BLACK);
        submit.setAlignmentX(LEFT_ALIGNMENT);
        submit.addActionListener(e -> submitReservation());
        card.add(submit);
        card.add(Box.createVerticalStrut(10));

        resStatus = UITheme.makeLabel("", UITheme.FONT_SMALL, UITheme.ACCENT_GREEN);
        resStatus.setAlignmentX(LEFT_ALIGNMENT);
        card.add(resStatus);

        outer.add(card);
        return outer;
    }

    private void submitReservation() {
        String sel  = (String) resEquipCombo.getSelectedItem();
        String eqID = sel.split(" — ")[0];
        Equipment eq = ds.findEquipmentByID(eqID);
        int days    = (int) resDateSpinner.getValue();

        Reservation res = new Reservation(
            student.getUniversityID(), student.getName(),
            eq.getEquipmentID(), eq.getName(),
            LocalDate.now().plusDays(days)
        );
        ds.reservations.add(res);
        resStatus.setForeground(UITheme.ACCENT_GREEN);
        resStatus.setText("✔ Reserved! ID: " + res.getReservationID());
    }

    // ─── Tab 6: Feedback ─────────────────────────────────────────────────────

    private JPanel buildFeedbackTab() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG_DARK);

        JPanel card = UITheme.makeCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(520, 360));

        JLabel title = UITheme.makeLabel("Submit Feedback / Complaint", UITheme.FONT_HEAD, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(20));

        card.add(UITheme.makeLabel("Type", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(Box.createVerticalStrut(5));
        JComboBox<String> typeCombo = UITheme.makeCombo(new String[]{"FEEDBACK", "COMPLAINT"});
        typeCombo.setAlignmentX(LEFT_ALIGNMENT);
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(typeCombo);
        card.add(Box.createVerticalStrut(14));

        card.add(UITheme.makeLabel("Message", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        card.add(Box.createVerticalStrut(5));
        JTextArea msgArea = new JTextArea(5, 30);
        msgArea.setBackground(UITheme.BG_HOVER);
        msgArea.setForeground(UITheme.TEXT_PRIMARY);
        msgArea.setCaretColor(UITheme.ACCENT_GREEN);
        msgArea.setFont(UITheme.FONT_BODY);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane sp = UITheme.makeScroll(msgArea);
        sp.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sp);
        card.add(Box.createVerticalStrut(16));

        JLabel status = UITheme.makeLabel("", UITheme.FONT_SMALL, UITheme.ACCENT_GREEN);
        status.setAlignmentX(LEFT_ALIGNMENT);

        JButton submit = UITheme.makeButton("  Submit  ", UITheme.ACCENT_BLUE, Color.WHITE);
        submit.setAlignmentX(LEFT_ALIGNMENT);
        submit.addActionListener(e -> {
            String msg = msgArea.getText().trim();
            if (msg.isEmpty()) { status.setForeground(UITheme.ACCENT_RED); status.setText("⚠ Message cannot be empty."); return; }
            Feedback fb = new Feedback(student.getUniversityID(), student.getName(), msg, (String) typeCombo.getSelectedItem());
            ds.feedbacks.add(fb);
            status.setForeground(UITheme.ACCENT_GREEN);
            status.setText("✔ Submitted! ID: " + fb.getFeedbackID());
            msgArea.setText("");
        });
        card.add(submit);
        card.add(Box.createVerticalStrut(8));
        card.add(status);

        outer.add(card);
        return outer;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void styleSpinner(JSpinner s) {
        s.setBackground(UITheme.BG_HOVER);
        s.setForeground(UITheme.TEXT_PRIMARY);
        s.setFont(UITheme.FONT_BODY);
        s.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
