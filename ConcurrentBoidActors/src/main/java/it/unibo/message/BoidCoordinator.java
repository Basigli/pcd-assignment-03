package it.unibo.message;

import akka.actor.Props;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;



public class BoidCoordinator extends AbstractBehavior<BoidMessage> {
    private ActorRef<BoidMessage> viewActor = null;
    private int boidCount = 0;
    private ActorRef<BoidMessage> model;
    private CoordinatorState state;

    private final int FRAMERATE = 25; // 25 frames per second
    private long t0 = System.currentTimeMillis();
    private int framerate;
    private int nBoids;



    public static Behavior<BoidMessage> create(ActorRef<BoidMessage> model) {
        return Behaviors.setup(context -> new BoidCoordinator(context, model));
    }
    private BoidCoordinator(ActorContext<BoidMessage> context, ActorRef<BoidMessage> model) {
        super(context);
        this.model = model;
        this.state = CoordinatorState.PAUSED;
        this.nBoids = 0;
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
                .onMessage(AttachView.class, this::onAttachView)
                .onMessage(BoidsResponse.class, this::onBoidsReceived)
                .build();
    }

    private Behavior<BoidMessage> onAttachView(AttachView attachView) {
        this.viewActor = attachView.viewActor;
        return this;
    }

    private Behavior<BoidMessage> onBoidsReceived(BoidsResponse message) {
        var boids = message.boids;
        this.nBoids = boids.size();
        switch (this.state) {
            case COMPUTING_VELOCITY -> {boids.forEach(boid -> boid.tell(new ComputeVelocity(null)));}
            case UPDATING_VELOCITY -> {boids.forEach(boid -> boid.tell(new UpdateVelocity(null)));}
            case UPDATING_POSITION -> {boids.forEach(boid -> boid.tell(new UpdatePosition(null)));}
        }

        return this;
    }

    // UGUALE A onStart -> in futuro sarà da unificare
    private Behavior<BoidMessage> onResume(Resume resume) {
        this.state = CoordinatorState.COMPUTING_VELOCITY;
        this.model.tell(new GetBoids(getContext().getSelf()));
        return this;
    }

    private Behavior<BoidMessage> onStop(Stop stop) {
        this.state = CoordinatorState.PAUSED;
        return this;
    }

    // UGUALE A onResume -> in futuro sarà da unificare
    private Behavior<BoidMessage> onStart(Start message) {
        this.state = CoordinatorState.COMPUTING_VELOCITY;
        this.model.tell(new GetBoids(getContext().getSelf()));
        return this;
    }

    private Behavior<BoidMessage> onVelocityComputed(VelocityComputed message) {
        boidCount++;
        if (boidCount == this.nBoids) {
            boidCount = 0; // reset count for next cycle
            this.state = CoordinatorState.UPDATING_VELOCITY;
            this.model.tell(new GetBoids(getContext().getSelf()));
        }
        return this;
    }

    private Behavior<BoidMessage> onVelocityUpdated(VelocityUpdated message) {
        boidCount++;
        if (boidCount == this.nBoids) {
            boidCount = 0;
            this.state = CoordinatorState.UPDATING_POSITION;
            this.model.tell(new GetBoids(getContext().getSelf()));
        }
        return this;
    }

    private Behavior<BoidMessage> onPositionUpdated(PositionUpdated message) {
        boidCount++;
        // if all boids have updated their position, notify the view actor
        // update GUI
        if (boidCount != this.nBoids)
            return this;

        boidCount = 0;
        viewActor.tell(new UpdateView(framerate));
        var t1 = System.currentTimeMillis();
        var dtElapsed = t1 - t0;
        var frameratePeriod = 1000 / FRAMERATE;
        if (dtElapsed < frameratePeriod) {
            try {
                Thread.sleep(frameratePeriod - dtElapsed);
            } catch (Exception ex) {
            }
            framerate = FRAMERATE;
        } else {
            framerate = (int) (1000 / dtElapsed);
        }
        t0 = System.currentTimeMillis();

        // if not paused, compute next velocity
        if (this.state != CoordinatorState.PAUSED) {
            this.state = CoordinatorState.COMPUTING_VELOCITY;
            this.model.tell(new GetBoids(getContext().getSelf()));
        }
        return this;
    }
}
