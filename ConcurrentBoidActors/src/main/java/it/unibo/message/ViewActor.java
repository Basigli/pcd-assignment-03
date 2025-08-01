package it.unibo.message;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import it.unibo.commmon.BoidsView;
import it.unibo.commmon.BoidsModel;
import it.unibo.message.BoidMessage.*;



public class ViewActor extends AbstractBehavior<BoidMessage> {

    private final BoidsView view;

    private ViewActor(ActorContext<BoidMessage> context, BoidsModel model, ActorRef<BoidMessage> coordinator, int screenWidth, int screenHeight) {
        super(context);
        this.view = new BoidsView(model, coordinator, screenWidth, screenHeight);
    }

    public static Behavior<BoidMessage> create(BoidsModel model, ActorRef<BoidMessage> coordinator, int screenWidth, int screenHeight) {
        return Behaviors.setup(context -> new ViewActor(context, model, coordinator, screenWidth, screenHeight));

    }

    @Override
    public Receive<BoidMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(UpdateView.class, this::onUpdateView)
                .build();
    }

    private Behavior<BoidMessage> onUpdateView(UpdateView updateView) {
        view.update(updateView.framerate());
        return this;
    }
}
