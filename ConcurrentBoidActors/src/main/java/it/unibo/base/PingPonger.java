package it.unibo.base;


import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.time.Duration;

public class PingPonger extends AbstractBehavior<PingPongMessage> {
    private int bounces;

    private PingPonger(ActorContext<PingPongMessage> context, int bounces) {
        super(context);
        this.bounces = bounces;
        context.getLog().info("Hello. My path is: " + context.getSelf().path());
    }

    public static Behavior<PingPongMessage> create(int bounces) {
        return Behaviors.setup(context -> new PingPonger(context, bounces));
    }

    @Override
    public Receive<PingPongMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(Ping.class, this::onPing)
                .onMessage(Pong.class, this::onPong)
                .build();
    }

    private Behavior<PingPongMessage> onPing(Ping message) {
        bounces--;
        if (bounces < 0) {
            getContext().getLog().info("I got tired of pingpong-ing. Bye bye.");
            return Behaviors.stopped();
        } else {
            getContext().getLog().info("Ping");
            getContext().scheduleOnce(
                    Duration.ofSeconds(1),
                    message.replyTo.unsafeUpcast(),
                    new Pong(getContext().getSelf().unsafeUpcast())
            );
            return this;
        }
    }

    private Behavior<PingPongMessage> onPong(Pong message) {
        bounces--;
        if (bounces < 0) {
            getContext().getLog().info("I got tired of pingpong-ing. Bye bye.");
            return Behaviors.stopped();
        } else {
            getContext().getLog().info("Pong");
            getContext().scheduleOnce(
                    Duration.ofSeconds(1),
                    message.replyTo.unsafeUpcast(),
                    new Ping(getContext().getSelf().unsafeUpcast())
            );
            return this;
        }
    }
}