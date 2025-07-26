package it.unibo.agar.view

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import it.unibo.agar.{GameOver, Message, UpdateWorld}

import scala.swing.Font.Dialog

object LocalViewActor:
  def apply(localView: LocalView): Behavior[Message] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case UpdateWorld(world) =>
          localView.world = Some(world)
          localView.repaint()
          Behaviors.same
        case GameOver(winnerId) =>
          println(s"Game ended, winner is ${winnerId}!")
          localView.close()
          Behaviors.same
      }
    }