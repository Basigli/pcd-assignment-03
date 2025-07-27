package it.unibo.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import it.unibo.commmon.Boid;
import it.unibo.commmon.BoidsModel;
import it.unibo.message.BoidMessage.*;

public class BoidActor extends AbstractBehavior<BoidMessage> {

    private final ActorRef<BoidMessage> coordinator;
    private final Boid boid;

    public static Behavior<BoidMessage> create(ActorRef<BoidMessage> coordinator, Boid boid) {
        return Behaviors.setup(context -> new BoidActor(context, coordinator, boid));
    }

    private  BoidActor(ActorContext<BoidMessage> context, ActorRef<BoidMessage> coordinator, Boid boid) {
        super(context);
        this.coordinator = coordinator;
        this.boid = boid;
    }

    @Override
    public Receive<BoidMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(ComputeVelocity.class, this::onComputeVelocity)
                .onMessage(UpdateVelocity.class, this::onUpdateVelocity)
                .onMessage(UpdatePosition.class, this::onUpdatePosition)
                .build();
    }

    private Behavior<BoidMessage> onComputeVelocity(ComputeVelocity message) {
        boid.computeVelocity(message.boids(), message.perceptionRadius(), message.avoidRadius());
        coordinator.tell(new VelocityComputed());
        return this;
    }

    private Behavior<BoidMessage> onUpdateVelocity(UpdateVelocity message) {
        boid.updateVelocity(message.alignmentWeight(), message.separationWeight(), message.cohesionWeight(), message.maxSpeed());
        coordinator.tell(new VelocityUpdated());
        return this;
    }

    private Behavior<BoidMessage> onUpdatePosition(UpdatePosition message) {
        boid.updatePos(message.minX(), message.maxX(), message.minY(), message.maxY(), message.width(), message.heigh());
        coordinator.tell(new PositionUpdated());
        return this;
    }
}
