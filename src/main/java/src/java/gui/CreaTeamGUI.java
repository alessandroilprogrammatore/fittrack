// File: CreaTeamGUI.java
package gui;

import controller.Controller;

import javax.swing.*;
import java.awt.*;
import gui.ButtonFactory;

public class CreaTeamGUI extends JFrame {
    private final Controller controller;
    private final JTextField nomeField = new JTextField(20);

    public CreaTeamGUI(Controller controller) {
        super("Crea Team");
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        panel.add(new JLabel("Nome Team:"), gbc);
        gbc.gridx = 1;
        panel.add(nomeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JButton createButton = ButtonFactory.createButton("Crea Team");
        createButton.addActionListener(e -> onCreate());
        panel.add(createButton, gbc);

        add(panel);
        pack();
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    private void onCreate() {
        String nome = nomeField.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci un nome per il team.", "Errore di input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (controller.creaTeam(nome) == null) {
            JOptionPane.showMessageDialog(this,
                    "Impossibile creare il team. Nome già usato o sei già in un team.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Team creato con successo.", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}
