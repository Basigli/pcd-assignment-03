package it.unibo.base;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;

public class Main {
    public static void main(String[] args) {
        ActorRef<PingPongMessage> system =
                ActorSystem.create(PingPonger.create(10), "pingPonger");
        system.tell(new Ping(system.unsafeUpcast()));
    }
}