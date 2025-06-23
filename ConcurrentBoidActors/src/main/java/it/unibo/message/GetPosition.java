package it.unibo.message;

import akka.actor.typed.ActorRef;

public class GetPosition implements BoidMessage {
    public ActorRef<BoidMessage> replyTo;
    public GetPosition(ActorRef<BoidMessage> replyTo) {
        this.replyTo = replyTo;
    }
}
