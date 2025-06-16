package it.unibo.message;

import akka.actor.Props;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import it.unibo.commmon.BoidsModel;
import it.unibo.commmon.BoidsView;

import java.util.List;

public class BoidCoordinator extends AbstractBehavior<BoidMessage> {
    private final List<ActorRef<BoidMessage>> boidActors;
    private final ActorRef<BoidMessage> viewActor;
    private int boidCount = 0;
    private boolean isPaused = false;


    private BoidCoordinator(ActorContext<BoidMessage> context, List<ActorRef<BoidMessage>> boidActors, ActorRef<BoidMessage> viewActor) {
        super(context);
        this.boidActors = boidActors;
        this.viewActor = viewActor;
    }


    @Override
    public Receive<BoidMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(Start.class, this::onStart)
                .onMessage(Stop.class, this::onStop)
                .onMessage(Resume.class, this::onResume)
                .onMessage(VelocityComputed.class, this::onVelocityComputed)
                .onMessage(VelocityUpdated.class, this::onVelocityUpdated)
                .onMessage(PositionUpdated.class, this::onPositionUpdated)
                .build();
    }

    private Behavior<BoidMessage> onResume(Resume resume) {
        isPaused = false;
        boidActors.forEach(boidActor -> boidActor.tell(new ComputeVelocity()));
        return this;
    }

    private Behavior<BoidMessage> onStop(Stop stop) {
        isPaused = true;
        return this;
    }


    private Behavior<BoidMessage> onStart(Start message) {
        boidActors.forEach(boidActor -> boidActor.tell(new ComputeVelocity()));
        return this;
    }

    private Behavior<BoidMessage> onVelocityComputed(VelocityComputed message) {
        boidCount++;
        if (boidCount == boidActors.size()) {
            boidCount = 0; // reset count for next cycle
            boidActors.forEach(boidActor -> boidActor.tell(new UpdateVelocity()));
        }
        return this;
    }

    private Behavior<BoidMessage> onVelocityUpdated(VelocityUpdated message) {
        boidCount++;
        if (boidCount == boidActors.size()) {
            boidCount = 0;
            boidActors.forEach(boidActor -> boidActor.tell(new UpdatePosition()));
        }
        return this;
    }

    private Behavior<BoidMessage> onPositionUpdated(PositionUpdated message) {
        boidCount++;
        // if all boids have updated their position, notify the view actor
        // update GUI
        if (boidCount == boidActors.size()) {
            boidCount = 0;
            viewActor.tell(new UpdateView());
        }
        // if not paused, compute next velocity
        if (!isPaused) {
            boidActors.forEach(boidActor -> boidActor.tell(new ComputeVelocity()));
        }
        return this;
    }
}
