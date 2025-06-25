package it.unibo.message;

import akka.actor.Props;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import it.unibo.commmon.Boid;
import it.unibo.commmon.BoidsModel;
import it.unibo.commmon.BoidsView;

import java.util.ArrayList;
import java.util.List;

public class BoidCoordinator extends AbstractBehavior<BoidMessage> {
    private List<ActorRef<BoidMessage>> boidActors = new ArrayList<>();
    private ActorRef<BoidMessage> viewActor = null;
    private int boidCount = 0;
    private boolean isPaused = false;
    private BoidsModel model;

    private final int FRAMERATE = 25; // 25 frames per second
    private long t0 = System.currentTimeMillis();
    private int framerate;

    public static Behavior<BoidMessage> create(BoidsModel model) {
        return Behaviors.setup(context -> new BoidCoordinator(context, model));
    }
    private BoidCoordinator(ActorContext<BoidMessage> context, BoidsModel model) {
        super(context);
        List<Boid> boids = model.getBoids();
        this.model = model;
        for (int i = 0; i < boids.size(); i++) {
            ActorRef<BoidMessage> boid = context.spawn(BoidActor.create(context.getSelf(), boids.get(i), model), "boid-" + i);
            boidActors.add(boid);
        }
    }


    @Override
    public Receive<BoidMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(Start.class, this::onStart)
                .onMessage(Stop.class, this::onStop)
                .onMessage(Resume.class, this::onResume)
                .onMessage(Reset.class, this::onReset)
                .onMessage(VelocityComputed.class, this::onVelocityComputed)
                .onMessage(VelocityUpdated.class, this::onVelocityUpdated)
                .onMessage(PositionUpdated.class, this::onPositionUpdated)
                .onMessage(AttachView.class, this::onAttachView)
                .build();
    }

    private Behavior<BoidMessage> onReset(Reset reset) {

        model.setNboids(0);
        model.setNboids(boidActors.size());
        boidActors.clear();
        for (var actor : boidActors) {
            getContext().stop(actor);
        }
        var boids = model.getBoids();
        for (int i = 0; i < boids.size(); i++) {
            ActorRef<BoidMessage> boid = getContext().spawnAnonymous(BoidActor.create(getContext().getSelf(), boids.get(i), model));
            boidActors.add(boid);
        }
        return this;
    }

    private Behavior<BoidMessage> onAttachView(AttachView attachView) {
        this.viewActor = attachView.viewActor;
        return this;
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
        if (boidCount != boidActors.size())
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
        if (!isPaused) {
            boidActors.forEach(boidActor -> boidActor.tell(new ComputeVelocity()));
        }




        return this;
    }
}
