// File: gui/MainMenuGUI.java
package gui;

import controller.Controller;
import model.Utente;
import model.Partecipante;
import model.Organizzatore;
import model.Giudice;
import model.Team;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * Finestra principale dopo il login: mostra azioni disponibili in base al ruolo
 */
public class MainMenuGUI extends JFrame {
    public MainMenuGUI(Controller controller) {
        super("Hackathon Manager");
        initUI(controller);
    }

    private void initUI(Controller controller) {
        // Look & Feel di sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Pannello principale
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(45, 62, 80));

        // Titolo
        JLabel title = new JLabel("Hackathon Manager", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Pannello dei bottoni
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));

        Utente u = controller.getCurrentUser();
        if (u instanceof Partecipante) {
            Team myTeam = controller.getTeamOfCurrentUser();
            if (myTeam == null) {
                JButton creaTeam = createStyledButton("Crea Team");
                creaTeam.addActionListener(e -> new CreaTeamGUI(controller));
                btnPanel.add(creaTeam);
            } else {
                JButton teamBtn = createStyledButton("Il mio Team");
                teamBtn.addActionListener(e -> new TeamGUI(controller));
                btnPanel.add(teamBtn);
            }
            btnPanel.add(Box.createVerticalStrut(10));

            JButton inviti = createStyledButton("Inviti");
            inviti.addActionListener(e -> new InvitiPartecipanteGUI((Partecipante) u, controller));
            btnPanel.add(inviti);

        } else if (u instanceof Organizzatore) {
            JButton creaHack = createStyledButton("Crea Hackathon");
            creaHack.addActionListener(e -> new CreaHackathonGUI(controller));
            btnPanel.add(creaHack);

        } else if (u instanceof Giudice) {
            JButton valuta = createStyledButton("Valuta Team");
            valuta.addActionListener(e -> new ValutaTeamGUI(controller));
            btnPanel.add(valuta);
        }

        btnPanel.add(Box.createVerticalStrut(20));
        JButton logout = createStyledButton("Logout");
        logout.addActionListener(e -> {
            new SignIn(controller);
            dispose();
        });
        btnPanel.add(logout);

        mainPanel.add(btnPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 Hackathon Manager", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    /**
     * Crea un JButton con stile personalizzato
     */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(10));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        return btn;
    }

    /**
     * Bordo arrotondato per i bottoni
     */
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
