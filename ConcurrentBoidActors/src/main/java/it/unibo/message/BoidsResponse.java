package it.unibo.message;

import akka.actor.typed.ActorRef;

import java.util.List;

public class BoidsResponse implements BoidMessage {
    public List<ActorRef<BoidMessage>> boids;
    public BoidsResponse(List<ActorRef<BoidMessage>> boids){this.boids = boids;}
}
