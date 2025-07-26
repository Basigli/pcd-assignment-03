package it.unibo.agar.model

import akka.actor.typed.{ActorRef, Behavior}
import it.unibo.agar.{Message, Tick}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration.FiniteDuration

object ClockActor:
  def apply(manager: ActorRef[Message], interval: FiniteDuration): Behavior[Message] =
    Behaviors.withTimers: timers => 
      timers.startTimerAtFixedRate("tick", Tick, interval)
      Behaviors.receiveMessage { _ =>
        manager ! Tick
        Behaviors.same
      }