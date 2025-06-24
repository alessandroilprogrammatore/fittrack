package gui;

import controller.Controller;
import model.Giudice;
import model.Organizzatore;
import model.Partecipante;
import model.Utente;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard principale: mostra azioni disponibili in base al ruolo.
 */
public class Dashboard extends JFrame {
    private final Utente utente;
    private final Controller controller;

    public Dashboard(Utente u, Controller controller) {
        super("Dashboard - Benvenuto");
        this.utente = u;
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setLocationRelativeTo(null);

        JLabel lbl = new JLabel("Ciao, " + utente.getNome() + " " + utente.getCognome() + "!");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 20f));
        add(lbl);

        // Se sei partecipante, puoi creare team e vedere inviti
        if (utente instanceof Partecipante) {
            JButton creaTeam = new JButton("Crea Team");
            creaTeam.addActionListener(e -> new CreaTeamGUI(controller));
            add(creaTeam);

            JButton inviti = new JButton("Inviti");
            inviti.addActionListener(e -> new InvitiPartecipanteGUI((Partecipante) utente, controller));
            add(inviti);

            // Se sei organizzatore, puoi creare hackathon
        } else if (utente instanceof Organizzatore) {
            JButton creaHack = new JButton("Crea Hackathon");
            creaHack.addActionListener(e -> new CreaHackathonGUI(controller));
            add(creaHack);

            // Se sei giudice, puoi valutare i team
        } else if (utente instanceof Giudice) {
            JButton valuta = new JButton("Valuta Team");
            valuta.addActionListener(e -> new ValutaTeamGUI(controller));
            add(valuta);
        }

        // Pulsante di logout
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            new SignIn(controller);
            dispose();
        });
        add(logout);

        pack();
        setVisible(true);
    }
}
