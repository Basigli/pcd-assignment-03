package it.unibo.agar;

import it.unibo.agar.model.RemoteGameStateManager;
import it.unibo.agar.view.RemoteLocalView;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    private static final long GAME_TICK_MS = 30; // Corresponds to ~33 FPS

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        String playerId = (args.length < 2) ? null : args[1];
        AtomicReference<RemoteLocalView> localView = new AtomicReference<>();
        try {
            var registry = LocateRegistry.getRegistry(host);
            var gameManager = (RemoteGameStateManager) registry.lookup("gameManager");
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
