package gui;

import data.DataStore;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField     idField;
    private JPasswordField passField;
    private JLabel         errorLabel;

    public LoginFrame() {
        UITheme.applyGlobalLook();
        setTitle("Sports Equipment Management System");
        setSize(500, 580);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        add(buildLeft(),  BorderLayout.WEST);
        add(buildForm(),  BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel buildLeft() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // gradient sidebar
                GradientPaint gp = new GradientPaint(0, 0, UITheme.ACCENT_GREEN.darker().darker(),
                        0, getHeight(), new Color(13, 17, 23));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // decorative circles
                g2.setColor(new Color(35, 197, 94, 30));
                g2.fillOval(-40, -40, 200, 200);
                g2.fillOval(-20, getHeight()-120, 160, 160);
                g2.dispose();
            }
        };
        p.setPreferredSize(new Dimension(140, 0));
        p.setLayout(new GridBagLayout());

        JLabel icon = new JLabel("🏆");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 40));
        JLabel text = UITheme.makeLabel("SEMS", new Font("SansSerif", Font.BOLD, 16), UITheme.ACCENT_GREEN);
        JLabel sub  = UITheme.makeLabel("Sports", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JLabel sub2 = UITheme.makeLabel("Equipment", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JLabel sub3 = UITheme.makeLabel("Management", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        icon.setAlignmentX(CENTER_ALIGNMENT);
        text.setAlignmentX(CENTER_ALIGNMENT);
        sub.setAlignmentX(CENTER_ALIGNMENT);
        sub2.setAlignmentX(CENTER_ALIGNMENT);
        sub3.setAlignmentX(CENTER_ALIGNMENT);
        inner.add(icon);
        inner.add(Box.createVerticalStrut(8));
        inner.add(text);
        inner.add(sub);
        inner.add(sub2);
        inner.add(sub3);
        p.add(inner);
        return p;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel();
        p.setBackground(UITheme.BG_DARK);
        p.setLayout(new GridBagLayout());
        p.setBorder(new EmptyBorder(40, 30, 40, 30));

        JPanel card = UITheme.makeCard(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(35, 35, 35, 35));
        card.setOpaque(false);

        JLabel title = UITheme.makeLabel("Welcome Back", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel sub   = UITheme.makeLabel("Sign in to your account", UITheme.FONT_BODY, UITheme.TEXT_MUTED);
        title.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));

        // ID field
        JLabel idLbl = UITheme.makeLabel("University ID", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        idLbl.setAlignmentX(LEFT_ALIGNMENT);
        idField = UITheme.makeField(20);
        idField.setAlignmentX(LEFT_ALIGNMENT);
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(idLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(idField);
        card.add(Box.createVerticalStrut(16));

        // Password field
        JLabel passLbl = UITheme.makeLabel("Password", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        passLbl.setAlignmentX(LEFT_ALIGNMENT);
        passField = UITheme.makePassField(20);
        passField.setAlignmentX(LEFT_ALIGNMENT);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(10));

        // Error label
        errorLabel = UITheme.makeLabel("", UITheme.FONT_SMALL, UITheme.ACCENT_RED);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(20));

        // Login button
        JButton loginBtn = UITheme.makeButton("  Sign In  →  ", UITheme.ACCENT_GREEN, Color.BLACK);
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> doLogin());
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(20));

        // Hint
        JLabel hint = UITheme.makeLabel("Demo — Student: 22P-4953 / pass123   |   Head: SH001 / head123",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        card.add(hint);

        // Enter key on password
        passField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        p.add(card);
        return p;
    }

    private void doLogin() {
        String id   = idField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (id.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("⚠ Please fill in all fields.");
            return;
        }

        User user = DataStore.getInstance().findUserByID(id);

        if (user == null) {
            errorLabel.setText("⚠ User not found.");
            return;
        }
        if (!user.isActive()) {
            errorLabel.setText("⚠ Account is deactivated. Contact Sports Head.");
            return;
        }
        if (!user.getPassword().equals(pass)) {
            errorLabel.setText("⚠ Incorrect password.");
            return;
        }

        // Route to dashboard
        dispose();
        if (user.getRole().equals("SPORTS_HEAD")) {
            new SportsHeadDashboard((SportsHead) user);
        } else {
            new StudentDashboard((Student) user);
        }
    }
}
