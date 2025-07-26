package it.unibo.agar.model

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import it.unibo.agar._

object GameManagerLookupActor:
  def apply(onFound: ActorRef[Message] => Unit, onNotFound: () => Unit, key: ServiceKey[Message]): Behavior[Receptionist.Listing] =
    Behaviors.receive { (ctx, msg) =>
      msg.serviceInstances(key).headOption match
        case Some(ref) => 
          println("PORCODIO TROVATA") 
          onFound(ref)
          Behaviors.stopped
        case None      => 
          onNotFound()
          Behaviors.same
    }