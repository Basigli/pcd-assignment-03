package it.unibo.agar.model


import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import it.unibo.agar.Message
import it.unibo.agar.*

trait GameStateManager:

  def getWorld: World
  def movePlayerDirection(id: String, dx: Double, dy: Double): Unit

object GameStateManagerActor:
  def apply(initialWorld: World,  globalViewActor: ActorRef[Message], speed: Double = 10.0): Behavior[Message] =
    Behaviors.setup { _ =>
      var world = initialWorld
      var directions: Map[String, (Double, Double)] = Map.empty
      var listeners: Map[String, ActorRef[Message]] = Map.empty
      var tickCount = 0
      val massLimit = 1000
      Behaviors.receiveMessage:
        case MovePlayerDirection(id, dx, dy) =>
          directions = directions.updated(id, (dx, dy))
          Behaviors.same

        case Tick =>
          tickCount += 1
          directions.foreach:
            case (id, (dx, dy)) =>
              world.playerById(id).foreach: player =>
                val newPlayerPosition = updatePlayerPosition(player, dx, dy, speed, world)
                world = updateWorldAfterMovement(newPlayerPosition, world)

          if tickCount % 20 == 0 then
            world = world.addRandomFood()
            tickCount = 0

          world.players.find(_.mass >= massLimit) match {
            case Some(winner) =>
              listeners.foreach: (_, listener) =>
                listener ! GameOver(winner.id)
            case None =>
              globalViewActor ! UpdateWorld(world)
              listeners.foreach: (_, listener) =>
                listener ! UpdateWorld(world)
          }
          Behaviors.same

        case AddPlayer(player, listener) =>
          println(s"Received AddPlayer with id: ${player.id}")
          world = world.addPlayer(player)
          println(s"Player with id ${world.playerById(player.id).get} added to the world")
          listeners = listeners.updated(player.id, listener)
          Behaviors.same

        case PlayerDisconnected(playerId) =>
          println(s"Received PlayerDisconnected with id: ${playerId}")
          world = world.removePlayer(playerId)
          listeners = listeners.removed(playerId)
          Behaviors.same
    }

  private def updatePlayerPosition(player: Player, dx: Double, dy: Double, speed: Double, world: World): Player =
    val newX = (player.x + dx * speed).max(0).min(world.width)
    val newY = (player.y + dy * speed).max(0).min(world.height)
    player.copy(x = newX, y = newY)

  private def updateWorldAfterMovement(player: Player, world: World): World =
    val foodEaten = world.foods.filter(food => EatingManager.canEatFood(player, food))
    val playerEatsFood = foodEaten.foldLeft(player)((p, food) => p.grow(food))
    val playersEaten = world
      .playersExcludingSelf(player)
      .filter(player => EatingManager.canEatPlayer(playerEatsFood, player))
    val playerEatPlayers = playersEaten.foldLeft(playerEatsFood)((p, other) => p.grow(other))
    world
      .updatePlayer(playerEatPlayers)
      .removePlayers(playersEaten)
      .removeFoods(foodEaten)