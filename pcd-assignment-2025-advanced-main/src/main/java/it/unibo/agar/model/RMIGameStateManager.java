package it.unibo.agar.model;

import java.rmi.RemoteException;

public class RMIGameStateManager implements RemoteGameStateManager {

    private World word;

    public RMIGameStateManager(World initialWorld) throws RemoteException {
        super();
        word = initialWorld;

    }

    @Override
    public World getWorld() throws RemoteException {
        return null;
    }

    @Override
    public void setPlayerDirection(String playerId, double dx, double dy) throws RemoteException {

    }

    @Override
    public void tick() throws RemoteException {

    }
}
