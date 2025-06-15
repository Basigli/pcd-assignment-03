package it.unibo.message;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class BoidCoordinator extends AbstractBehavior<BoidMessage> {
    private BoidCoordinator(ActorContext<BoidMessage> context) {
        super(context);
    }

    @Override
    public Receive<BoidMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(Start.class, this::onStart)
                .onMessage(VelocityComputed.class, this::onVelocityComputed)
                .onMessage(VelocityUpdated.class, this::onVelocityUpdated)
                .onMessage(PositionUpdated.class, this::onPositionUpdated)
                .build();
    }


    private Behavior<BoidMessage> onStart(Start message) {
        return this;
    }

    private Behavior<BoidMessage> onVelocityComputed(VelocityComputed message) {
        return this;
    }

    private Behavior<BoidMessage> onVelocityUpdated(VelocityUpdated message) {
        return this;
    }

    private Behavior<BoidMessage> onPositionUpdated(PositionUpdated message) {
        return this;
    }



}
