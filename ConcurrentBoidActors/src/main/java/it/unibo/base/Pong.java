package it.unibo.base;

import akka.actor.typed.ActorRef;

public final class Pong implements PingPongMessage {
    public final ActorRef<Ping> replyTo;

    public Pong(ActorRef<Ping> replyTo) {
        this.replyTo = replyTo;
    }
}