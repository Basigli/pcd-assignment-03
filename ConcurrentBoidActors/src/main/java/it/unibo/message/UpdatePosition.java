package it.unibo.message;

import it.unibo.commmon.BoidsModel;

public class UpdatePosition implements BoidMessage {
    public BoidsModel model;
    public UpdatePosition(BoidsModel model) {this.model = model;}

}
