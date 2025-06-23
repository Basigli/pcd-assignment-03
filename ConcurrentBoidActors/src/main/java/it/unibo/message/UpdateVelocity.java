package it.unibo.message;

import it.unibo.commmon.BoidsModel;

public class UpdateVelocity implements BoidMessage {

    public BoidsModel model;

    public UpdateVelocity(BoidsModel model) {this.model = model;}


}
