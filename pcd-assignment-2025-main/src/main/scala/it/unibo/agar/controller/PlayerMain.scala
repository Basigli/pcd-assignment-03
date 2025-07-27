package it.unibo.agar.controller

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.cluster.{Cluster, ClusterEvent}
import com.typesafe.config.ConfigFactory
import it.unibo.agar.Message
import it.unibo.agar.controller.Main.{gameStateManagerRef, system}
import it.unibo.agar.model.{GameManagerLookupActor, GameStateManagerActor, Player}
import it.unibo.agar.view.{LocalView, LocalViewActor}
import it.unibo.agar.*
import jdk.jpackage.internal.Arguments.CLIOptions.context

import scala.concurrent.ExecutionContext.Implicits.global
import java.awt.Window
import java.util.{Timer, TimerTask}
import scala.swing.Swing.onEDT
import scala.swing.{Frame, SimpleSwingApplication}
import scala.util.Random

object PlayerMain extends SimpleSwingApplication:

  private val port = 20000 + Random.nextInt(10000)
  private val width = 1000
  private val height = 1000
  private val initialMass = 120

  private val player = Player(s"p${Random.nextInt(100000)}", Random.nextInt(width), Random.nextInt(height), initialMass)
  private val localView = LocalView(playerId = player.id)
  private val config = ConfigFactory
    .parseString(s"""akka.remote.artery.canonical.port=${port}""")
    .withFallback(ConfigFactory.load("agario"))
  private val GameManagerKey = ServiceKey[Message]("GameManager")
  private val gameManagerRefPromise = scala.concurrent.Promise[ActorRef[Message]]()
  private val system = ActorSystem(Behaviors.empty, "agario", config)
  private val lookupActor = system.systemActorOf(
    GameManagerLookupActor(ref => gameManagerRefPromise.success(ref), () => println("not found"), GameManagerKey),
    "gameManagerLookup"
  )
  private val localViewActor = system.systemActorOf(
    LocalViewActor(localView), "LocalViewActor"
  )


  system.receptionist ! Receptionist.Find(GameManagerKey, replyTo = lookupActor)

  private val lookupInterval = 500 // ms
  private val lookupTimer = new Timer()
  private val lookupTask = new TimerTask:
    override def run(): Unit =
      system.receptionist ! Receptionist.Find(GameManagerKey, replyTo = lookupActor)


  lookupTimer.scheduleAtFixedRate(lookupTask, 0, lookupInterval)

  gameManagerRefPromise.future.foreach { gameManagerRef =>
    lookupTimer.cancel()
    println("gameManagerRef fetched")
    gameManagerRef ! AddPlayer(player, localViewActor)
    localView.gameStateManager = Some(gameManagerRef)
  }

  override def top: Frame = {
    localView.open()
    new Frame {visible = false}
  }

