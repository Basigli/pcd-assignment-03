package it.unibo.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import it.unibo.commmon.BoidsModel;
import it.unibo.message.BoidMessage.AttachView;

public class BoidsSimulation {
    final static int N_BOIDS = 5000; // 1500;
    final static double SEPARATION_WEIGHT = 1.0;
    final static double ALIGNMENT_WEIGHT = 1.0;
    final static double COHESION_WEIGHT = 1.0;

    final static int ENVIRONMENT_WIDTH = 1000;
    final static int ENVIRONMENT_HEIGHT = 1000;
    static final double MAX_SPEED = 4.0;
    static final double PERCEPTION_RADIUS = 50.0;
    static final double AVOID_RADIUS = 20.0;

    final static int SCREEN_WIDTH = 800;
    final static int SCREEN_HEIGHT = 800;


    public static void main(String[] args) {
        var model = new BoidsModel(
                N_BOIDS,
                SEPARATION_WEIGHT, ALIGNMENT_WEIGHT, COHESION_WEIGHT,
                ENVIRONMENT_WIDTH, ENVIRONMENT_HEIGHT,
                MAX_SPEED,
                PERCEPTION_RADIUS,
                AVOID_RADIUS);

        ActorSystem<BoidMessage> coordinator = ActorSystem.create(BoidCoordinator.create(model), "Coordinator");
        ActorRef<BoidMessage> viewActor = coordinator.systemActorOf(
                ViewActor.create(model, coordinator, SCREEN_WIDTH, SCREEN_HEIGHT), "ViewActor", Props.empty());
        coordinator.tell(new AttachView(viewActor));
    }
}
