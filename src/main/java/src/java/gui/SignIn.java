// File: src/main/java/gui/SignIn.java
package gui;

import controller.Controller;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import gui.ButtonFactory;

public class SignIn extends JFrame {
    private final Controller controller;
    private final JTextField emailField = new JTextField();
    private final JPasswordField pwdField = new JPasswordField();

    public SignIn(Controller controller) {
        super("Login - Hackathon Manager");
        this.controller = controller;
        initUI();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        // Look & Feel di sistema
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        GradientPanel mainPanel = new GradientPanel(30, 45, 60);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Titolo
        JLabel title = new JLabel("Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        mainPanel.add(title, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel emailLbl = new JLabel("Email:");
        emailLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailLbl.setForeground(Color.WHITE);
        formPanel.add(emailLbl, gbc);
        gbc.gridx = 1;
        emailField.setPreferredSize(new Dimension(240, 36));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel pwdLbl = new JLabel("Password:");
        pwdLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pwdLbl.setForeground(Color.WHITE);
        formPanel.add(pwdLbl, gbc);
        gbc.gridx = 1;
        pwdField.setPreferredSize(new Dimension(240, 36));
        pwdField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(pwdField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton loginBtn = ButtonFactory.createButton("Accedi");
        loginBtn.setPreferredSize(new Dimension(140,44));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD,18));
        loginBtn.addActionListener(e -> onLogin());
        // Hover verde
        Color loginOrig = loginBtn.getBackground();
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { loginBtn.setBackground(Color.GREEN); }
            @Override public void mouseExited(MouseEvent e) { loginBtn.setBackground(loginOrig); }
        });
        buttonPanel.add(loginBtn);

        JButton regBtn = ButtonFactory.createButton("Registrati");
        regBtn.setPreferredSize(new Dimension(140,44));
        regBtn.setFont(new Font("Segoe UI", Font.BOLD,18));
        regBtn.addActionListener(e -> {
            dispose();
            new Registrazione(controller);
        });
        // Hover rosso
        Color regOrig = regBtn.getBackground();
        regBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { regBtn.setBackground(Color.RED); }
            @Override public void mouseExited(MouseEvent e) { regBtn.setBackground(regOrig); }
        });
        buttonPanel.add(regBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ESC per tornare al menu
        getRootPane().registerKeyboardAction(e -> {
            dispose();
            new MainMenuGUI(controller);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setContentPane(mainPanel);
    }

    private void onLogin() {
        String email = emailField.getText().trim();
        String pwd = String.valueOf(pwdField.getPassword());
        if (controller.login(email, pwd) == null) {
            JOptionPane.showMessageDialog(this,
                    "Credenziali non valide.", "Errore", JOptionPane.ERROR_MESSAGE);
        } else {
            // Chiudi il login e riapri il menu principale
            dispose();
            MainMenuGUI menu = new MainMenuGUI(controller);
            menu.setVisible(true);
        }
    }

    // Gradiente di sfondo
    private static class GradientPanel extends JPanel {
        private final Color start, end;
        public GradientPanel(int r, int g, int b) { start=new Color(r,g,b); end=new Color(20,30,50);}
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g; int w=getWidth(), h=getHeight();
            GradientPaint gp=new GradientPaint(0,0,start,0,h,end); g2.setPaint(gp);
            g2.fillRect(0,0,w,h);
        }
    }
}
