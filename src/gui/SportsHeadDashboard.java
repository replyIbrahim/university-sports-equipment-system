package gui;

import data.DataStore;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class SportsHeadDashboard extends JFrame {

    private final SportsHead head;
    private final DataStore  ds = DataStore.getInstance();

    // Requests tab
    private DefaultTableModel requestsModel;
    private JTable            requestsTable;

    // Equipment tab
    private DefaultTableModel equipModel;
    private JTable            equipTable;
    private JTextField nameField, qtyField;
    private JComboBox<String> sportCombo, condCombo, catCombo;

    // Users tab
    private DefaultTableModel usersModel;

    // Reports tab
    private JLabel totalLbl, availLbl, borrowedLbl, maintenanceLbl, retiredLbl;

    // Feedback tab
    private DefaultTableModel feedbackModel;

    public SportsHeadDashboard(SportsHead head) {
        this.head = head;
        setTitle("Sports Head Dashboard — " + head.getName());
        setSize(1050, 680);
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
            BorderFactory.createMatteBorder(0,0,1,0, UITheme.BORDER_COLOR),
            new EmptyBorder(14, 20, 14, 20)
        ));
        JLabel logo = UITheme.makeLabel("🏆  Sports Equipment Management", UITheme.FONT_HEAD, UITheme.TEXT_PRIMARY);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JLabel badge = UITheme.makeLabel("⚙  SPORTS HEAD", UITheme.FONT_SMALL, UITheme.ACCENT_ORANGE);
        JLabel user  = UITheme.makeLabel(head.getName(), UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        JButton out  = UITheme.makeButton("Logout", UITheme.ACCENT_RED, Color.WHITE);
        out.addActionListener(e -> { dispose(); new LoginFrame(); });
        right.add(badge); right.add(user); right.add(out);
        p.add(logo, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ─── Tabs ─────────────────────────────────────────────────────────────────

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);

        tabs.addTab("📋  Borrow Requests",   buildRequestsTab());
        tabs.addTab("📦  Manage Equipment",  buildEquipmentTab());
        tabs.addTab("👥  Manage Users",      buildUsersTab());
        tabs.addTab("📊  Inventory Report",  buildReportTab());
        tabs.addTab("💬  Feedback",          buildFeedbackTab());
        return tabs;
    }

    // ─── Tab 1: Borrow Requests ───────────────────────────────────────────────

    private JPanel buildRequestsTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        requestsModel = new DefaultTableModel(
            new String[]{"Record ID","Borrower","Equipment","Qty","Borrow Date","Return Date","Status"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        requestsTable = new JTable(requestsModel);
        UITheme.styleTable(requestsTable);
        refreshRequests();

        JButton approve = UITheme.makeButton("✔ Approve", UITheme.ACCENT_GREEN, Color.BLACK);
        JButton reject  = UITheme.makeButton("✘ Reject",  UITheme.ACCENT_RED, Color.WHITE);
        JButton refresh = UITheme.makeButton("⟳ Refresh", UITheme.ACCENT_BLUE, Color.WHITE);
        JButton maint   = UITheme.makeButton("🔧 Flag Maintenance", UITheme.ACCENT_ORANGE, Color.BLACK);

        approve.addActionListener(e -> approveRequest());
        reject.addActionListener(e  -> rejectRequest());
        refresh.addActionListener(e -> refreshRequests());
        maint.addActionListener(e   -> flagMaintenance());

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.add(approve); bar.add(reject); bar.add(maint); bar.add(refresh);

        p.add(bar, BorderLayout.NORTH);
        p.add(UITheme.makeScroll(requestsTable), BorderLayout.CENTER);

        JLabel hint = UITheme.makeLabel("  Select a row, then click Approve / Reject / Flag Maintenance",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        p.add(hint, BorderLayout.SOUTH);
        return p;
    }

    private void refreshRequests() {
        requestsModel.setRowCount(0);
        for (BorrowRecord br : ds.borrowRecords) {
            requestsModel.addRow(new Object[]{
                br.getRecordID(), br.getBorrowerName(), br.getEquipmentName(),
                br.getQuantity(), br.getBorrowDate(), br.getExpectedReturnDate(), br.getStatus()
            });
        }
    }

    private void approveRequest() {
        int row = requestsTable.getSelectedRow();
        if (row < 0) { showInfo("Select a request first."); return; }
        String rid = (String) requestsModel.getValueAt(row, 0);
        BorrowRecord br = findRecord(rid);
        if (br == null || !br.getStatus().equals("PENDING")) {
            showInfo("Only PENDING requests can be approved."); return;
        }
        Equipment eq = ds.findEquipmentByID(br.getEquipmentID());
        if (eq == null || !eq.borrowQty(br.getQuantity())) {
            showInfo("Not enough equipment available."); return;
        }
        head.approveRequest(br);
        showInfo("✔ Request " + rid + " APPROVED.");
        refreshRequests();
    }

    private void rejectRequest() {
        int row = requestsTable.getSelectedRow();
        if (row < 0) { showInfo("Select a request first."); return; }
        String rid = (String) requestsModel.getValueAt(row, 0);
        BorrowRecord br = findRecord(rid);
        if (br == null || !br.getStatus().equals("PENDING")) {
            showInfo("Only PENDING requests can be rejected."); return;
        }
        String remarks = JOptionPane.showInputDialog(this, "Enter rejection reason:", "Reject", JOptionPane.PLAIN_MESSAGE);
        if (remarks == null) return;
        head.rejectRequest(br, remarks);
        showInfo("Request " + rid + " REJECTED.");
        refreshRequests();
    }

    private void flagMaintenance() {
        int row = requestsTable.getSelectedRow();
        if (row < 0) { showInfo("Select a row first."); return; }
        String eqName = (String) requestsModel.getValueAt(row, 2);
        Equipment eq = ds.equipment.stream().filter(e -> e.getName().equals(eqName)).findFirst().orElse(null);
        if (eq == null) { showInfo("Equipment not found."); return; }
        eq.flagMaintenance();
        showInfo("🔧 " + eqName + " flagged for maintenance.\nRepair count: " + eq.getRepairCount() +
                (eq.getCondition().equals("Retired") ? "\n⚠ Equipment RETIRED after 5 repairs!" : ""));
        refreshRequests();
    }

    private BorrowRecord findRecord(String rid) {
        return ds.borrowRecords.stream().filter(r -> r.getRecordID().equals(rid)).findFirst().orElse(null);
    }

    // ─── Tab 2: Manage Equipment ──────────────────────────────────────────────

    private JPanel buildEquipmentTab() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        // LEFT: Add form
        JPanel formCard = UITheme.makeCard(14);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        formCard.setOpaque(false);
        formCard.setPreferredSize(new Dimension(250, 0));

        JLabel formTitle = UITheme.makeLabel("Add Equipment", UITheme.FONT_HEAD, UITheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(16));

        formCard.add(UITheme.makeLabel("Name", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        formCard.add(Box.createVerticalStrut(4));
        nameField = UITheme.makeField(16);
        nameField.setAlignmentX(LEFT_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(nameField);
        formCard.add(Box.createVerticalStrut(10));

        formCard.add(UITheme.makeLabel("Sport Type", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        formCard.add(Box.createVerticalStrut(4));
        sportCombo = UITheme.makeCombo(new String[]{"Cricket","Football","Basketball","Badminton","Table Tennis","Volleyball","Hockey"});
        sportCombo.setAlignmentX(LEFT_ALIGNMENT);
        sportCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(sportCombo);
        formCard.add(Box.createVerticalStrut(10));

        formCard.add(UITheme.makeLabel("Category", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        formCard.add(Box.createVerticalStrut(4));
        catCombo = UITheme.makeCombo(new String[]{"Ball","Bat","Racket","Stick","Clothing","Protection","Footwear","Other"});
        catCombo.setAlignmentX(LEFT_ALIGNMENT);
        catCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(catCombo);
        formCard.add(Box.createVerticalStrut(10));

        formCard.add(UITheme.makeLabel("Quantity", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        formCard.add(Box.createVerticalStrut(4));
        qtyField = UITheme.makeField(6);
        qtyField.setText("1");
        qtyField.setAlignmentX(LEFT_ALIGNMENT);
        qtyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(qtyField);
        formCard.add(Box.createVerticalStrut(10));

        formCard.add(UITheme.makeLabel("Condition", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        formCard.add(Box.createVerticalStrut(4));
        condCombo = UITheme.makeCombo(new String[]{"Good","Fair","Damaged"});
        condCombo.setAlignmentX(LEFT_ALIGNMENT);
        condCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        formCard.add(condCombo);
        formCard.add(Box.createVerticalStrut(16));

        JButton addBtn = UITheme.makeButton("  + Add Equipment  ", UITheme.ACCENT_GREEN, Color.BLACK);
        addBtn.setAlignmentX(LEFT_ALIGNMENT);
        addBtn.addActionListener(e -> addEquipment());
        formCard.add(addBtn);

        formCard.add(Box.createVerticalStrut(10));
        JButton delBtn = UITheme.makeButton("  − Remove Selected  ", UITheme.ACCENT_RED, Color.WHITE);
        delBtn.setAlignmentX(LEFT_ALIGNMENT);
        delBtn.addActionListener(e -> removeEquipment());
        formCard.add(delBtn);

        formCard.add(Box.createVerticalStrut(10));
        JButton maintDone = UITheme.makeButton("✔ Complete Maintenance", UITheme.ACCENT_ORANGE, Color.BLACK);
        maintDone.setAlignmentX(LEFT_ALIGNMENT);
        maintDone.addActionListener(e -> completeMaintenance());
        formCard.add(maintDone);

        // RIGHT: Table
        equipModel = new DefaultTableModel(
            new String[]{"ID","Name","Sport","Category","Available","Total","Condition","Status","Repairs"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        equipTable = new JTable(equipModel);
        UITheme.styleTable(equipTable);
        refreshEquipTable();

        p.add(formCard,                      BorderLayout.WEST);
        p.add(UITheme.makeScroll(equipTable), BorderLayout.CENTER);
        return p;
    }

    private void addEquipment() {
        String name = nameField.getText().trim();
        String qtyStr = qtyField.getText().trim();
        if (name.isEmpty()) { showInfo("Name cannot be empty."); return; }
        int qty;
        try { qty = Integer.parseInt(qtyStr); if (qty < 1) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { showInfo("Enter a valid quantity (≥ 1)."); return; }

        Equipment eq = new Equipment(
            name, (String) sportCombo.getSelectedItem(),
            (String) catCombo.getSelectedItem(), qty,
            (String) condCombo.getSelectedItem()
        );
        ds.equipment.add(eq);
        nameField.setText("");
        qtyField.setText("1");
        showInfo("✔ Equipment added! ID: " + eq.getEquipmentID());
        refreshEquipTable();
    }

    private void removeEquipment() {
        int row = equipTable.getSelectedRow();
        if (row < 0) { showInfo("Select equipment to remove."); return; }
        String id = (String) equipModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove equipment " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        ds.equipment.removeIf(e -> e.getEquipmentID().equals(id));
        showInfo("Equipment removed.");
        refreshEquipTable();
    }

    private void completeMaintenance() {
        int row = equipTable.getSelectedRow();
        if (row < 0) { showInfo("Select equipment first."); return; }
        String id = (String) equipModel.getValueAt(row, 0);
        Equipment eq = ds.findEquipmentByID(id);
        if (eq == null) return;
        if (!eq.getStatus().equals("Under Maintenance")) { showInfo("Equipment is not Under Maintenance."); return; }
        eq.completeMaintenance();
        showInfo("✔ Maintenance complete. " + eq.getName() + " is now Available.");
        refreshEquipTable();
    }

    private void refreshEquipTable() {
        equipModel.setRowCount(0);
        for (Equipment e : ds.equipment) {
            equipModel.addRow(new Object[]{
                e.getEquipmentID(), e.getName(), e.getSportType(), e.getCategory(),
                e.getAvailableQuantity(), e.getTotalQuantity(),
                e.getCondition(), e.getStatus(), e.getRepairCount()
            });
        }
    }

    // ─── Tab 3: Manage Users ──────────────────────────────────────────────────

    private JPanel buildUsersTab() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Left: Add user form
        JPanel card = UITheme.makeCard(14);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(240, 0));

        JLabel title = UITheme.makeLabel("Register Student", UITheme.FONT_HEAD, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(14));

        JTextField[] fields = new JTextField[5];
        String[] labels = {"University ID","Password","Full Name","Contact","Department"};
        for (int i = 0; i < 5; i++) {
            card.add(UITheme.makeLabel(labels[i], UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
            card.add(Box.createVerticalStrut(4));
            fields[i] = UITheme.makeField(16);
            fields[i].setAlignmentX(LEFT_ALIGNMENT);
            fields[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            card.add(fields[i]);
            card.add(Box.createVerticalStrut(10));
        }

        JButton regBtn = UITheme.makeButton("  Register  ", UITheme.ACCENT_GREEN, Color.BLACK);
        regBtn.setAlignmentX(LEFT_ALIGNMENT);
        regBtn.addActionListener(e -> {
            for (JTextField f : fields) if (f.getText().trim().isEmpty()) { showInfo("Fill all fields."); return; }
            if (ds.findUserByID(fields[0].getText().trim()) != null) { showInfo("ID already exists."); return; }
            Student s = new Student(fields[0].getText().trim(), fields[1].getText().trim(),
                fields[2].getText().trim(), fields[3].getText().trim(), fields[4].getText().trim());
            ds.users.add(s);
            for (JTextField f : fields) f.setText("");
            showInfo("✔ Student registered!");
            refreshUsersTable();
        });
        card.add(regBtn);

        // Right: users table + toggle
        usersModel = new DefaultTableModel(
            new String[]{"ID","Name","Role","Department","Active"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable t = new JTable(usersModel);
        UITheme.styleTable(t);
        refreshUsersTable();

        JButton toggle = UITheme.makeButton("Toggle Active/Inactive", UITheme.ACCENT_ORANGE, Color.BLACK);
        toggle.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) { showInfo("Select a user."); return; }
            String uid = (String) usersModel.getValueAt(row, 0);
            User u = ds.findUserByID(uid);
            if (u == null || u.getRole().equals("SPORTS_HEAD")) { showInfo("Cannot toggle Sports Head."); return; }
            u.setActive(!u.isActive());
            showInfo("User " + u.getName() + " is now " + (u.isActive() ? "ACTIVE" : "INACTIVE"));
            refreshUsersTable();
        });

        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.setOpaque(false);
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.add(toggle);
        right.add(bar, BorderLayout.NORTH);
        right.add(UITheme.makeScroll(t), BorderLayout.CENTER);

        p.add(card, BorderLayout.WEST);
        p.add(right, BorderLayout.CENTER);
        return p;
    }

    private void refreshUsersTable() {
        usersModel.setRowCount(0);
        for (User u : ds.users) {
            usersModel.addRow(new Object[]{u.getUniversityID(), u.getName(), u.getRole(), u.getDepartment(), u.isActive() ? "Yes" : "No"});
        }
    }

    // ─── Tab 4: Inventory Report ──────────────────────────────────────────────

    private JPanel buildReportTab() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = UITheme.makeLabel("Inventory Report", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);

        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 14, 0));
        statsPanel.setOpaque(false);

        totalLbl      = new JLabel();
        availLbl      = new JLabel();
        borrowedLbl   = new JLabel();
        maintenanceLbl= new JLabel();
        retiredLbl    = new JLabel();

        JPanel total   = UITheme.statCard("—", "Total Items",      UITheme.ACCENT_BLUE);
        JPanel avail   = UITheme.statCard("—", "Available",        UITheme.ACCENT_GREEN);
        JPanel borrowed= UITheme.statCard("—", "Borrowed",         UITheme.ACCENT_ORANGE);
        JPanel maint   = UITheme.statCard("—", "Under Maintenance",UITheme.ACCENT_ORANGE);
        JPanel retired = UITheme.statCard("—", "Retired",          UITheme.ACCENT_RED);

        statsPanel.add(total); statsPanel.add(avail); statsPanel.add(borrowed);
        statsPanel.add(maint); statsPanel.add(retired);

        JButton genBtn = UITheme.makeButton("  Generate Report  ", UITheme.ACCENT_BLUE, Color.WHITE);
        genBtn.addActionListener(e -> generateReport(total, avail, borrowed, maint, retired));

        // Detail table
        DefaultTableModel repModel = new DefaultTableModel(
            new String[]{"Sport","Total Equip","Available","Borrowed","Under Maintenance"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable repTable = new JTable(repModel);
        UITheme.styleTable(repTable);

        genBtn.addActionListener(e -> {
            repModel.setRowCount(0);
            String[] sports = {"Cricket","Football","Basketball","Badminton","Table Tennis","Volleyball","Hockey"};
            for (String sport : sports) {
                List<Equipment> list = ds.getEquipmentBySport(sport);
                if (list.isEmpty()) continue;
                long avl  = list.stream().filter(eq -> eq.getStatus().equals("Available")).count();
                long bor  = list.stream().filter(eq -> eq.getStatus().equals("Unavailable")).count();
                long man  = list.stream().filter(eq -> eq.getStatus().equals("Under Maintenance")).count();
                repModel.addRow(new Object[]{sport, list.size(), avl, bor, man});
            }
        });

        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.setOpaque(false);
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titleRow.setOpaque(false);
        titleRow.add(title); titleRow.add(genBtn);
        top.add(titleRow,  BorderLayout.NORTH);
        top.add(statsPanel, BorderLayout.CENTER);

        p.add(top,                          BorderLayout.NORTH);
        p.add(UITheme.makeScroll(repTable), BorderLayout.CENTER);

        // Auto generate on tab load
        SwingUtilities.invokeLater(() -> genBtn.doClick());
        return p;
    }

    private void generateReport(JPanel... cards) {
        long total   = ds.equipment.size();
        long avail   = ds.equipment.stream().filter(e -> e.getStatus().equals("Available")).count();
        long borrowed= ds.equipment.stream().filter(e -> e.getStatus().equals("Unavailable")).count();
        long maint   = ds.equipment.stream().filter(e -> e.getStatus().equals("Under Maintenance")).count();
        long retired = ds.equipment.stream().filter(e -> e.getCondition().equals("Retired")).count();

        updateCard(cards[0], String.valueOf(total),   "Total Items");
        updateCard(cards[1], String.valueOf(avail),   "Available");
        updateCard(cards[2], String.valueOf(borrowed), "Borrowed");
        updateCard(cards[3], String.valueOf(maint),   "Under Maintenance");
        updateCard(cards[4], String.valueOf(retired), "Retired");
    }

    private void updateCard(JPanel card, String value, String label) {
        Component[] comps = card.getComponents();
        for (Component c : comps) {
            if (c instanceof JPanel) {
                JPanel inner = (JPanel) c;
                for (Component ic : inner.getComponents()) {
                    if (ic instanceof JLabel) {
                        JLabel lbl = (JLabel) ic;
                        if (lbl.getFont().getSize() > 20) lbl.setText(value);
                    }
                }
            }
        }
        card.repaint();
    }

    // ─── Tab 5: Feedback ─────────────────────────────────────────────────────

    private JPanel buildFeedbackTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));

        feedbackModel = new DefaultTableModel(
            new String[]{"ID","Submitter","Type","Message","Resolved"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable t = new JTable(feedbackModel);
        UITheme.styleTable(t);
        refreshFeedbacks();

        JButton refresh = UITheme.makeButton("⟳ Refresh", UITheme.ACCENT_BLUE, Color.WHITE);
        refresh.addActionListener(e -> refreshFeedbacks());

        JButton resolve = UITheme.makeButton("✔ Mark Resolved", UITheme.ACCENT_GREEN, Color.BLACK);
        resolve.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) { showInfo("Select feedback first."); return; }
            String fid = (String) feedbackModel.getValueAt(row, 0);
            ds.feedbacks.stream().filter(f -> f.getFeedbackID().equals(fid)).findFirst().ifPresent(f -> {
                f.resolve();
                showInfo("✔ Feedback resolved.");
                refreshFeedbacks();
            });
        });

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bar.setOpaque(false);
        bar.add(refresh); bar.add(resolve);

        p.add(bar, BorderLayout.NORTH);
        p.add(UITheme.makeScroll(t), BorderLayout.CENTER);
        return p;
    }

    private void refreshFeedbacks() {
        feedbackModel.setRowCount(0);
        for (Feedback fb : ds.feedbacks) {
            feedbackModel.addRow(new Object[]{
                fb.getFeedbackID(), fb.getSubmitterName(), fb.getType(),
                fb.getMessage().length() > 60 ? fb.getMessage().substring(0, 60) + "..." : fb.getMessage(),
                fb.isResolved() ? "Yes" : "No"
            });
        }
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
