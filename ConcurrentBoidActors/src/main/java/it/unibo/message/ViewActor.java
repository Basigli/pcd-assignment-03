package it.unibo.message;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import it.unibo.commmon.BoidsView;
import it.unibo.commmon.BoidsModel;

public class ViewActor extends AbstractBehavior<BoidMessage> {

    private final BoidsView view;
    private final BoidsModel model;


    public ViewActor(ActorContext<BoidMessage> context, BoidsView view, BoidsModel model) {
        super(context);
        this.view = view;
        this.model = model;
    }

    @Override
    public Receive<BoidMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(UpdateView.class, this::onUpdateView)
                .build();
    }

    private Behavior<BoidMessage> onUpdateView(UpdateView updateView) {
        view.update(30);    // this will be computed in the future
        return this;
    }
}
