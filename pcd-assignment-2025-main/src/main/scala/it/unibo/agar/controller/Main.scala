package it.unibo.agar.controller

import it.unibo.agar.model.{ ClockActor, GameInitializer, GameStateManagerActor, World}
import it.unibo.agar.view.{GlobalView, GlobalViewActor, LocalView}

import java.awt.Window
import java.util.Timer
import java.util.TimerTask
import scala.swing.*
import scala.swing.Swing.onEDT
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import com.typesafe.config.ConfigFactory
import it.unibo.agar.*
import akka.actor.typed.ActorRef
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.AskPattern.*

import scala.concurrent.duration.*
import scala.concurrent.Await
import akka.util.Timeout
import it.unibo.agar.{Message, Tick}

given Timeout = Timeout(3.seconds)


object Main extends SimpleSwingApplication:

  private val width = 1000
  private val height = 1000
  private val tickInterval = 30.millis
  private val numFoods = 100  // initial value, food will be generated dynamically
  private val foods = GameInitializer.initialFoods(numFoods, width, height)
  private val world = World(width = width, height = height, foods = foods)
  private var gameStateManagerRef: Option[ActorRef[Message]] = None
  private var globalView = GlobalView(world)
  private val config = ConfigFactory
    .parseString(s"""akka.remote.artery.canonical.port=25251""")
    .withFallback(ConfigFactory.load("agario"))

  private val GameManagerKey = ServiceKey[Message]("GameManager")
  private val system = ActorSystem(Behaviors.setup[Nothing] { ctx =>
    val globalViewActor = ctx.spawn(GlobalViewActor(globalView), "GlobalViewActor")
    val gameManager = ctx.spawn(GameStateManagerActor(world, globalViewActor), "GameStateManager")
    val clock = ctx.spawn(ClockActor(gameManager, tickInterval), "ClockActor")
    ctx.system.receptionist ! Receptionist.Register(GameManagerKey, gameManager)
    gameStateManagerRef = Some(gameManager)
    Behaviors.empty
  }, "agario", config)
  

  override def top: Frame =
    globalView.open()
    new Frame { visible = false }

