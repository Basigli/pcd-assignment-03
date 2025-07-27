package it.unibo.agar.view

import akka.actor.typed.{ActorRef, ActorSystem}
import it.unibo.agar.model.{GameStateManagerActor, World}
import it.unibo.agar.*

import java.awt.Graphics2D
import scala.concurrent.Await
import scala.swing.*
import scala.concurrent.duration.DurationInt


class LocalView(var gameStateManager: Option[ActorRef[Message]] = None, playerId: String, var world: Option[World] = None) extends MainFrame:

  title = s"Agar.io - Local View ($playerId)"
  preferredSize = new Dimension(400, 400)

  override def closeOperation(): Unit =
    gameStateManager.foreach(_ ! PlayerDisconnected(playerId))
    super.closeOperation()

  contents = new Panel:
    listenTo(keys, mouse.moves)
    focusable = true
    requestFocusInWindow()

    override def paintComponent(g: Graphics2D): Unit =
      world match
        case Some(world) =>
          val playerOpt = world.players.find(_.id == playerId)
          val (offsetX, offsetY) = playerOpt
            .map(p => (p.x - size.width / 2.0, p.y - size.height / 2.0))
            .getOrElse((0.0, 0.0))
          AgarViewUtils.drawWorld(g, world, offsetX, offsetY)
        case _ => ()


    reactions += { case e: event.MouseMoved =>
      val mousePos = e.point
      world match
        case Some(world) =>
          val playerOpt = world.players.find(_.id == playerId)
          playerOpt.foreach: player =>
            val dx = (mousePos.x - size.width / 2) * 0.01
            val dy = (mousePos.y - size.height / 2) * 0.01
            gameStateManager match
              case Some(manager) => manager ! MovePlayerDirection(playerId, dx, dy)
              case _ => ()
          repaint()
        case _ => ()
    }


