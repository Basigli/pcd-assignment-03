package it.unibo.base;

import akka.actor.typed.ActorRef;

public final class Ping implements PingPongMessage {
    public final ActorRef<Pong> replyTo;

    public Ping(ActorRef<Pong> replyTo) {
        this.replyTo = replyTo;
    }
}