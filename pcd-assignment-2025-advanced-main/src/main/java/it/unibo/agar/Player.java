package it.unibo.agar;

import it.unibo.agar.model.RemoteGameStateManager;

import java.rmi.registry.LocateRegistry;

public class Player {
     
    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            var registry = LocateRegistry.getRegistry(host);
            var gameManager = (RemoteGameStateManager) registry.lookup("gameManager");

        } catch (Exception e) {
            log("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }


    private static void log(String msg) {
        System.out.println("[ " + System.currentTimeMillis() + " ][ Player ] " + msg);
    }
}
