package it.unibo.agar.view;

import it.unibo.agar.model.Player;
import it.unibo.agar.model.RemoteGameStateManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Optional;

public class RemoteLocalView extends JFrame {
    private static final double SENSITIVITY = 2;
    private final RemoteGamePanel gamePanel;
    private final RemoteGameStateManager gameStateManager;
    private final String playerId;

    public RemoteLocalView(RemoteGameStateManager gameStateManager, String playerId) {
        this.gameStateManager = gameStateManager;
        this.playerId = playerId;

        setTitle("Agar.io - Local View (" + playerId + ") (Java)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Dispose only this window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    gameStateManager.removePlayer(playerId);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });


        setPreferredSize(new Dimension(600, 600));

        this.gamePanel = new RemoteGamePanel(gameStateManager, playerId);
        add(this.gamePanel, BorderLayout.CENTER);

        setupMouseControls();

        pack();
        setLocationRelativeTo(null); // Center on screen
    }

    private void setupMouseControls() {
        gamePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Optional<Player> playerOpt = null;
                try {
                    var world = gameStateManager.getWorld();
                    playerOpt = world.getPlayerById(playerId);

                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
                if (playerOpt.isPresent()) {
                    Point mousePos = e.getPoint();
                    // Player is always in the center of the local view
                    double viewCenterX = gamePanel.getWidth() / 2.0;
                    double viewCenterY = gamePanel.getHeight() / 2.0;

                    double dx = mousePos.x - viewCenterX;
                    double dy = mousePos.y - viewCenterY;

                    // Normalize the direction vector
                    double magnitude = Math.hypot(dx, dy);
                    if (magnitude > 0) { // Avoid division by zero if mouse is exactly at center
                        try {
                            gameStateManager.setPlayerDirection(playerId, (dx / magnitude) * SENSITIVITY, (dy / magnitude) * SENSITIVITY);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        try {
                            gameStateManager.setPlayerDirection(playerId, 0, 0); // Stop if mouse is at center
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    // Repainting is handled by the main game loop timer
                }
            }
        });
    }

    public void repaintView() {
        if (gamePanel != null) {
            gamePanel.repaint();
            try {
                var world = gameStateManager.getWorld();
                var playerOpt = world.getPlayerById(playerId);
                if (gameStateManager.isGameOver()) {
                    System.out.println("Game over");
                    dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                    return;
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }
}
