package it.unibo.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import it.unibo.commmon.Boid;
import it.unibo.commmon.BoidsModel;

public class BoidActor extends AbstractBehavior<BoidMessage> {

    private final ActorRef<BoidMessage> coordinator;
    private final Boid boid;
    private final BoidsModel model;

    public static Behavior<BoidMessage> create(ActorRef<BoidMessage> coordinator, Boid boid, BoidsModel model) {
        return Behaviors.setup(context -> new BoidActor(context, coordinator, boid, model));
    }

    private BoidActor(ActorContext<BoidMessage> context, ActorRef<BoidMessage> coordinator, Boid boid, BoidsModel model) {
        super(context);
        this.coordinator = coordinator;
        this.boid = boid;
        this.model = model;
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
        boid.computeVelocity(model);
        coordinator.tell(new VelocityComputed());
        return this;
    }

    private Behavior<BoidMessage> onUpdateVelocity(UpdateVelocity message) {
        boid.updateVelocity(model);
        coordinator.tell(new VelocityUpdated());
        return this;
    }

    private Behavior<BoidMessage> onUpdatePosition(UpdatePosition message) {
        boid.updatePos(model);
        coordinator.tell(new PositionUpdated());
        return this;
    }
}
