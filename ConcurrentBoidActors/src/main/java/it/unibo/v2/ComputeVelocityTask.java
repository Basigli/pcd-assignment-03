package it.unibo.v2;

import it.unibo.commmon.Boid;
import it.unibo.commmon.BoidsModel;

import java.util.concurrent.Callable;

public class ComputeVelocityTask implements Callable<Void> {
    private final Boid boid;
    private final BoidsModel model;

    public ComputeVelocityTask(Boid boid, BoidsModel model) {
        this.boid = boid;
        this.model = model;
    }
    @Override
    public Void call() throws Exception {
        this.boid.computeVelocity(this.model);
        return null;
    }
}
