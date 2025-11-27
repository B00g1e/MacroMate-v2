package me.bugi.macromate.server;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class MacroMateServer {
    public static void main(String[] args) {
        // Set FlatLaf Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the server on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}
