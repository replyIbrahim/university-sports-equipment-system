package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class UITheme {

    // ─── Color Palette ────────────────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(13,  17,  23);
    public static final Color BG_CARD       = new Color(22,  27,  34);
    public static final Color BG_HOVER      = new Color(30,  38,  48);
    public static final Color ACCENT_GREEN  = new Color(35, 197, 94);
    public static final Color ACCENT_BLUE   = new Color(56, 139, 253);
    public static final Color ACCENT_ORANGE = new Color(255, 153, 0);
    public static final Color ACCENT_RED    = new Color(248, 81, 73);
    public static final Color TEXT_PRIMARY  = new Color(230, 237, 243);
    public static final Color TEXT_MUTED    = new Color(139, 148, 158);
    public static final Color BORDER_COLOR  = new Color(48,  54,  61);

    // ─── Fonts ────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("SansSerif", Font.BOLD,   22);
    public static final Font FONT_HEAD   = new Font("SansSerif", Font.BOLD,   15);
    public static final Font FONT_BODY   = new Font("SansSerif", Font.PLAIN,  13);
    public static final Font FONT_SMALL  = new Font("SansSerif", Font.PLAIN,  11);
    public static final Font FONT_MONO   = new Font("Monospaced", Font.PLAIN, 12);

    // ─── Factory Methods ──────────────────────────────────────────────────────

    public static void applyGlobalLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("Panel.background",              BG_DARK);
        UIManager.put("OptionPane.background",         BG_CARD);
        UIManager.put("OptionPane.messageForeground",  TEXT_PRIMARY);
    }

    public static JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed())
                    g2.setColor(bg.darker());
                else if (getModel().isRollover())
                    g2.setColor(bg.brighter());
                else
                    g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 36));
        return btn;
    }

    public static JTextField makeField(int cols) {
        JTextField f = new JTextField(cols);
        f.setBackground(BG_HOVER);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT_GREEN);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    public static JPasswordField makePassField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setBackground(BG_HOVER);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT_GREEN);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    public static JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(BG_HOVER);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return cb;
    }

    public static JTable makeTable(String[] columns, Object[][] data) {
        JTable t = new JTable(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        styleTable(t);
        return t;
    }

    public static void styleTable(JTable t) {
        t.setBackground(BG_CARD);
        t.setForeground(TEXT_PRIMARY);
        t.setFont(FONT_BODY);
        t.setRowHeight(32);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setGridColor(BORDER_COLOR);
        t.setSelectionBackground(ACCENT_BLUE.darker());
        t.setSelectionForeground(Color.WHITE);
        t.getTableHeader().setBackground(BG_HOVER);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
    }

    public static JScrollPane makeScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_CARD);
        return sp;
    }

    public static JPanel makeCard(int arc) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);
                g2.dispose();
            }
        };
    }

    public static JPanel statCard(String value, String label, Color accent) {
        JPanel card = makeCard(14);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 22, 18, 22));
        card.setOpaque(false);

        JLabel bar = new JLabel("▐");
        bar.setForeground(accent);
        bar.setFont(new Font("SansSerif", Font.BOLD, 28));

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        JLabel val = makeLabel(value, new Font("SansSerif", Font.BOLD, 26), TEXT_PRIMARY);
        JLabel lbl = makeLabel(label, FONT_SMALL, TEXT_MUTED);
        text.add(val);
        text.add(lbl);

        card.add(bar, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }
}
