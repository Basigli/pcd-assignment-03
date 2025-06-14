package it.unibo.v2;


import it.unibo.commmon.Boid;
import it.unibo.commmon.BoidsModel;

import java.util.concurrent.Callable;

public class UpdatePositionTask implements Callable<Void> {

    private final Boid boid;
    private final BoidsModel model;

    public UpdatePositionTask(Boid boid, BoidsModel model) {
        this.boid = boid;
        this.model = model;
    }

    @Override
    public Void call() throws Exception {
        this.boid.updatePos(this.model);
        return null;
    }
}
