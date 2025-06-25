package it.unibo.message;

public class BoidsChanged implements BoidMessage {
    public int nBoids;

    public BoidsChanged(int nBoids) {this.nBoids = nBoids;}
}
