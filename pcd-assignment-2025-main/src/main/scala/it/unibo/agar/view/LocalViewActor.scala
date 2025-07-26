package it.unibo.agar.view

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import it.unibo.agar.{Message, UpdateWorld}

object LocalViewActor:
  def apply(localView: LocalView): Behavior[Message] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case UpdateWorld(world) =>
          localView.world = Some(world)
          localView.repaint()
          Behaviors.same
      }
    }