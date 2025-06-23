package it.unibo.message;

import akka.actor.typed.ActorRef;

public class GetBoids implements BoidMessage {
    public ActorRef<BoidMessage> replyTo;

    public GetBoids(ActorRef<BoidMessage> replyTo) {this.replyTo = replyTo;}
}
