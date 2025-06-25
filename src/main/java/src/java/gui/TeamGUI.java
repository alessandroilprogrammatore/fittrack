package gui;

import controller.Controller;
import model.Team;

import javax.swing.*;
import java.awt.*;

/**
 * Mostra i dettagli del team dell'utente e permette di aggiungere membri.
 */
public class TeamGUI extends JFrame {
    private final Controller controller;
    private final Team team;
    private final DefaultListModel<String> memberModel = new DefaultListModel<>();

    public TeamGUI(Controller controller) {
        super("Il mio Team");
        this.controller = controller;
        this.team = controller.getTeamOfCurrentUser();
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(400, 300);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (team == null) {
            panel.add(new JLabel("Non fai parte di alcun team.", SwingConstants.CENTER),
                    BorderLayout.CENTER);
            setContentPane(panel);
            setVisible(true);
            return;
        }

        JLabel title = new JLabel("Team: " + team.getNome(), SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(title, BorderLayout.NORTH);

        for (var p : team.getPartecipanti()) {
            memberModel.addElement(p.getNome() + " " + p.getCognome());
        }
        JList<String> list = new JList<>(memberModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel invitePanel = new JPanel(new FlowLayout());
        JTextField emailField = new JTextField(15);
        JButton inviteBtn = new JButton("Aggiungi membro");
        inviteBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) return;
            if (controller.aggiungiMembro(team, email)) {
                memberModel.addElement(email);
                emailField.setText("");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Impossibile aggiungere membro. Team pieno o email errata.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
        invitePanel.add(emailField);
        invitePanel.add(inviteBtn);
        panel.add(invitePanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setVisible(true);
    }
}
