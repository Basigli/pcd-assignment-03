package it.unibo.agar.model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteGameStateManager extends Remote {

    World getWorld() throws RemoteException;
    public void setPlayerDirection(String playerId, double dx, double dy) throws RemoteException;
    public void tick() throws RemoteException;
    public void addPlayer(Player player) throws RemoteException;
    void removePlayer(String playerId) throws RemoteException;
    boolean isGameOver() throws RemoteException;
}
