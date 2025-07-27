package it.unibo.agar;

import it.unibo.agar.model.Player;
import it.unibo.agar.model.RemoteGameStateManager;
import it.unibo.agar.view.RemoteLocalView;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    private static final long GAME_TICK_MS = 30; // Corresponds to ~33 FPS
    private static final Random random = new Random();
    private static final double INITIAL_MASS = 120.0;
    private static final int WORLD_WIDTH = 1000;
    private static final int WORLD_HEIGHT = 1000;

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        String playerId = "P-" + Integer.toHexString(random.nextInt(0xFFFFF));
        AtomicReference<RemoteLocalView> localView = new AtomicReference<>();
        try {
            var registry = LocateRegistry.getRegistry(host);
            var gameManager = (RemoteGameStateManager) registry.lookup("gameManager");
            var player = new Player(playerId, random.nextInt(WORLD_WIDTH), random.nextInt(WORLD_HEIGHT), INITIAL_MASS);
            gameManager.addPlayer(player);
            SwingUtilities.invokeLater(() -> {
                localView.set(new RemoteLocalView(gameManager, playerId));
                localView.get().setVisible(true);
            });
        } catch (Exception e) {
            log("Client exception: " + e.toString());
            e.printStackTrace();
        }
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    localView.get().repaintView();
                });
            }
        }, 0, GAME_TICK_MS);
    }

    private static void log(String msg) {
        System.out.println("[ " + System.currentTimeMillis() + " ][ Player ] " + msg);
    }
}
