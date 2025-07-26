package it.unibo.agar.view

import akka.actor.typed.{ActorRef, ActorSystem}
import it.unibo.agar.model.{GameStateManagerActor, World}
import it.unibo.agar.*

import java.awt.Graphics2D
import scala.concurrent.Await
import scala.swing.*
import scala.concurrent.duration.DurationInt

class LocalView(val gameStateManager: ActorRef[Message], playerId: String, var world: World)(using system: ActorSystem[?]) extends MainFrame:

  title = s"Agar.io - Local View ($playerId)"
  preferredSize = new Dimension(400, 400)

  contents = new Panel:
    listenTo(keys, mouse.moves)
    focusable = true
    requestFocusInWindow()

    override def paintComponent(g: Graphics2D): Unit =
      val playerOpt = world.players.find(_.id == playerId)
      val (offsetX, offsetY) = playerOpt
        .map(p => (p.x - size.width / 2.0, p.y - size.height / 2.0))
        .getOrElse((0.0, 0.0))
      AgarViewUtils.drawWorld(g, world, offsetX, offsetY)

    reactions += { case e: event.MouseMoved =>
      val mousePos = e.point
      val playerOpt = world.players.find(_.id == playerId)
      playerOpt.foreach: player =>
        val dx = (mousePos.x - size.width / 2) * 0.01
        val dy = (mousePos.y - size.height / 2) * 0.01
        gameStateManager ! MovePlayerDirection(playerId, dx, dy)
      repaint()
    }
