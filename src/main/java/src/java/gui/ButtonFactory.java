package gui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Utility per creare bottoni con uno stile uniforme e moderno.
 */
public final class ButtonFactory {
    private ButtonFactory() {}

    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        // Blu brillante per un contrasto maggiore
        Color base = new Color(0, 123, 255);
        btn.setBackground(base);
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(10));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(base.brighter());
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(base);
            }
        });
        return btn;
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        RoundedBorder(int r) { this.radius = r; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
        }
    }
}
