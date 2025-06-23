package it.unibo.message;


import it.unibo.commmon.P2d;

public class BoidPositionResponse implements BoidMessage {
    public P2d position;

    public BoidPositionResponse(P2d position) {
        this.position = position;
    }

}
