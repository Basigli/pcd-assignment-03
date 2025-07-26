package it.unibo.agar.view;

import it.unibo.agar.model.RemoteGameStateManager;

import javax.swing.*;
import java.awt.*;

public class RemoteGlobalView extends JFrame {

    private final RemoteGamePanel gamePanel;

    public RemoteGlobalView(RemoteGameStateManager gameStateManager) {
        setTitle("Agar.io - Global View (Java)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Or DISPOSE_ON_CLOSE if multiple windows
        setPreferredSize(new Dimension(800, 800));

        this.gamePanel = new RemoteGamePanel(gameStateManager);
        add(this.gamePanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public void repaintView() {
        if (gamePanel != null) {
            gamePanel.repaint();
        }
    }
}
