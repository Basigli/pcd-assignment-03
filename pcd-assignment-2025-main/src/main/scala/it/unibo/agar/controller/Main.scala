package it.unibo.agar.controller

import it.unibo.agar.model.{AIMovement, GameInitializer, GameStateManagerActor, MockGameStateManager, World}
import it.unibo.agar.view.GlobalView
import it.unibo.agar.view.LocalView

import java.awt.Window
import java.util.Timer
import java.util.TimerTask
import scala.swing.*
import scala.swing.Swing.onEDT
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory
import it.unibo.agar.model.GameStateManagerActor.Tick
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.util.Timeout

given Timeout = Timeout(3.seconds)


object Main extends SimpleSwingApplication:

  private val width = 1000
  private val height = 1000
  // private val numPlayers = 4  // no more necessary
  private val numFoods = 100  // initial value, food will be generated dynamically
  // private val players = GameInitializer.initialPlayers(numPlayers, width, height)     // no more necessary
  private val foods = GameInitializer.initialFoods(numFoods, width, height)
  private val world = World(width = width, height = height, foods = foods)
  // private val manager = new MockGameStateManager(World(width = width, height = height, foods = foods))
  private var gameStateManagerRef: Option[ActorRef[GameStateManagerActor.Command]] = None

  val config = ConfigFactory.load("agario.conf")

  val guardian = Behaviors.setup[Nothing] { context =>
    val gameStateManager = context.spawn(
      GameStateManagerActor(world),
      "gameStateManager"
    )
    gameStateManagerRef = Some(gameStateManager)
    Behaviors.empty
  }

  val system = ActorSystem[Nothing](guardian, "agario", config)

  private val timer = new Timer()
  private val task: TimerTask = new TimerTask:
    override def run(): Unit =
      //AIMovement.moveAI("p1", manager)
      //manager.tick()
      gameStateManagerRef.get ! Tick
      onEDT(Window.getWindows.foreach(_.repaint()))
  timer.scheduleAtFixedRate(task, 0, 30) // every 30ms

  override def top: Frame =
    // Open both views at startup
    new GlobalView(gameStateManagerRef.get)(using system).open()
    // new LocalView(manager, "p1").open()
    // new LocalView(manager, "p2").open()
    // No launcher window, just return an empty frame (or null if allowed)
    new Frame { visible = false }
