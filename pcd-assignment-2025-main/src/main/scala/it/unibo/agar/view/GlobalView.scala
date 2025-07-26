package it.unibo.agar.view

import akka.actor.typed.{ActorRef, ActorSystem}
import it.unibo.agar.Message
import it.unibo.agar.model.{GameStateManagerActor, World}
import it.unibo.agar._

import java.awt.Color
import java.awt.Graphics2D
import scala.concurrent.{Await, Future}
import scala.swing.*
import scala.concurrent.duration.DurationInt

class GlobalView(var world: World) extends MainFrame:
  title = "Agar.io - Global View"
  preferredSize = new Dimension(800, 800)

  contents = new Panel:
    override def paintComponent(g: Graphics2D): Unit =
      AgarViewUtils.drawWorld(g, world)