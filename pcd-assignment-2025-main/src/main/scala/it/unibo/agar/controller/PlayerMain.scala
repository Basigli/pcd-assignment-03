package it.unibo.agar.controller

import akka.actor.typed.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import it.unibo.agar.view.LocalView

import scala.swing.{Frame, SimpleSwingApplication}

object PlayerMain extends SimpleSwingApplication:


  val config = ConfigFactory.load("agario.conf")
  val system = ActorSystem[Nothing](akka.actor.typed.scaladsl.Behaviors.empty[Nothing], "agario-player", config)
  val gameStateManagerRef: ActorRef[?] = ??? // Ottieni il riferimento remoto

  override def top: Frame =
    new LocalView(null, "player1").open()
    new Frame {visible = true}