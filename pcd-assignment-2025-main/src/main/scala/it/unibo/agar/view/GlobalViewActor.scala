package it.unibo.agar.view

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import it.unibo.agar.{Message, UpdateWorld}

object GlobalViewActor:
  def apply(globalView: GlobalView): Behavior[Message] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case UpdateWorld(world) =>
          globalView.world = world
          globalView.repaint()
          Behaviors.same
      }
    }