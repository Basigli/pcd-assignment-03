package it.unibo.message;

import akka.actor.typed.ActorRef;


public class AttachView implements BoidMessage {

    public ActorRef<BoidMessage> viewActor;
    public AttachView(ActorRef<BoidMessage> viewActor) {
        this.viewActor = viewActor;
    }
}
