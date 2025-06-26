// File: src/main/java/model/Main.java
package model;

import javax.swing.SwingUtilities;
import controller.Controller;
import gui.MainMenuGUI;

/**
 * Punto d'ingresso dell'applicazione.
 */
public class Main {
    public static void main(String[] args) {
        // Crea il controller che utilizza il database
        Controller ctrl = new Controller();

        // Avvia la GUI principale sul thread Swing
        SwingUtilities.invokeLater(() -> {
            MainMenuGUI menu = new MainMenuGUI(ctrl);
            menu.setVisible(true);
        });
    }
}
