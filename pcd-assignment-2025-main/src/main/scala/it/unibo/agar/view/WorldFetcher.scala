package it.unibo.agar.view

import akka.actor.typed.{ActorRef, ActorSystem}
import it.unibo.agar.model.{GameStateManagerActor, World}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import akka.util.Timeout
import it.unibo.agar.Message
import it.unibo.agar._

trait WorldFetcher(using system: ActorSystem[?]):
  def gameStateManager: ActorRef[Message]

  def getWorld: Future[World] =
    import akka.actor.typed.scaladsl.AskPattern._
    given Timeout = Timeout(3.seconds)
    gameStateManager.ask(GetWorld(_))