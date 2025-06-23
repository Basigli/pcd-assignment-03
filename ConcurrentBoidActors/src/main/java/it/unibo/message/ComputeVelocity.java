package it.unibo.message;

import it.unibo.commmon.BoidsModel;

public class ComputeVelocity implements BoidMessage {

    public BoidsModel model;

    public ComputeVelocity(BoidsModel model) {this.model = model;}
}
