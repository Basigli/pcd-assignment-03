package it.unibo.agar;

import it.unibo.agar.model.*;
import it.unibo.agar.view.GlobalView;
import it.unibo.agar.view.LocalView;
import it.unibo.agar.view.RemoteGlobalView;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AgarIoServer {
    private static final int WORLD_WIDTH = 1000;
    private static final int WORLD_HEIGHT = 1000;
    private static final int NUM_FOODS = 100;
    private static final long GAME_TICK_MS = 30; // Corresponds



    public static void main(String[] args) throws RemoteException {
        // final List<Player> initialPlayers = GameInitializer.initialPlayers(NUM_PLAYERS, WORLD_WIDTH, WORLD_HEIGHT);

        final List<Food> initialFoods = GameInitializer.initialFoods(NUM_FOODS, WORLD_WIDTH, WORLD_HEIGHT);
        final World initialWorld = new World(WORLD_WIDTH, WORLD_HEIGHT, initialFoods);
        final RemoteGameStateManager gameManager = new RMIGameStateManager(initialWorld);

        // List to keep track of active views for repainting
        final List<Main.JFrameRepaintable> views = new ArrayList<>();


        try {
            var gameManagerStub = (RemoteGameStateManager) UnicastRemoteObject.exportObject(gameManager, 0);
            // var registry = LocateRegistry.getRegistry();
            var registry = LocateRegistry.createRegistry(1099);
            registry.rebind("gameManager", gameManagerStub);
            log("gameManager object registered.");
        } catch (Exception e) {
            log("exception: " + e.toString());
        }


        SwingUtilities.invokeLater(() -> {
            // GlobalView globalView = new GlobalView(gameManager);
            RemoteGlobalView globalView = new RemoteGlobalView(gameManager);
            views.add(globalView::repaintView); // Add repaint method reference
            globalView.setVisible(true);

            /*
            // LocalView localViewP1 = new LocalView(gameManager, "p1");
            LocalView localViewP1 = new LocalView(null, "p1");
            views.add(localViewP1::repaintView);
            localViewP1.setVisible(true);

            // LocalView localViewP2 = new LocalView(gameManager, "p2");
            LocalView localViewP2 = new LocalView(null, "p2");
            views.add(localViewP2::repaintView);
            localViewP2.setVisible(true);
            */
        });


        final java.util.Timer timer = new Timer(true); // Use daemon thread for timer
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // AI movement for p1, p3, p4
                // AIMovement.moveAI("p1", gameManager);
                // AIMovement.moveAI("p3", gameManager); // Assuming p3 is AI
                // AIMovement.moveAI("p4", gameManager); // Assuming p4 is AI
                try {
                    gameManager.tick();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

                SwingUtilities.invokeLater(() -> {
                    for (Main.JFrameRepaintable view : views) {
                        view.repaintView();
                    }
                });
            }
        }, 0, GAME_TICK_MS);
    }

    // Functional interface for repaintable views
    @FunctionalInterface
    interface JFrameRepaintable {
        void repaintView();
    }

    private static void log(String msg) {
        System.out.println("[ " + System.currentTimeMillis() + " ][ Server ] " + msg);
    }
}

