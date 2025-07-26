package it.unibo.agar.view

import akka.actor.typed.{ActorRef, ActorSystem}
import it.unibo.agar.model.{GameStateManagerActor, MockGameStateManager, World}

import java.awt.Color
import java.awt.Graphics2D
import scala.concurrent.{Await, Future}
import scala.swing.*
import scala.concurrent.duration.DurationInt

class GlobalView(val gameStateManager: ActorRef[GameStateManagerActor.Command])(using system: ActorSystem[?]) extends MainFrame with WorldFetcher:
  title = "Agar.io - Global View"
  preferredSize = new Dimension(800, 800)



  contents = new Panel:
    override def paintComponent(g: Graphics2D): Unit =
      val futureWorld = getWorld
      val world = Await.result(futureWorld, 3.seconds)
      AgarViewUtils.drawWorld(g, world)

