package it.unibo.agar

import akka.actor.typed.ActorRef
import akka.serialization.jackson.JsonSerializable
import it.unibo.agar.model.{Player, World}

/** Tag interface for all messages sends by actors */
trait Message extends JsonSerializable
case class MovePlayerDirection(id: String, dx: Double, dy: Double) extends Message
case object Tick extends Message
case class AddPlayer(player: Player, replyTo: ActorRef[Message]) extends Message
case class UpdateWorld(world: World) extends Message
case class PlayerDisconnected(playerId: String) extends Message
case class GameOver(winnerId: String) extends Message