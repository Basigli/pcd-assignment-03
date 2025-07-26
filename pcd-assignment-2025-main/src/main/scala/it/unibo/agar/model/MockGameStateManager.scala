package it.unibo.agar.model

import GameStateManagerActor.Command
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

trait GameStateManager:

  def getWorld: World
  def movePlayerDirection(id: String, dx: Double, dy: Double): Unit

class MockGameStateManager(var world: World, val speed: Double = 10.0) extends GameStateManager:

  private var directions: Map[String, (Double, Double)] = Map.empty
  def getWorld: World = world

  // Move a player in a given direction (dx, dy)
  def movePlayerDirection(id: String, dx: Double, dy: Double): Unit =
    directions = directions.updated(id, (dx, dy))

  def tick(): Unit =
    directions.foreach:
      case (id, (dx, dy)) =>
        world.playerById(id) match
          case Some(player) =>
            world = updateWorldAfterMovement(updatePlayerPosition(player, dx, dy))
          case None =>
          // Player not found, ignore movement

  private def updatePlayerPosition(player: Player, dx: Double, dy: Double): Player =
    val newX = (player.x + dx * speed).max(0).min(world.width)
    val newY = (player.y + dy * speed).max(0).min(world.height)
    player.copy(x = newX, y = newY)

  private def updateWorldAfterMovement(player: Player): World =
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

object GameStateManagerActor:
  sealed trait Command
  case class GetWorld(replyTo: ActorRef[World]) extends Command
  case class MovePlayerDirection(id: String, dx: Double, dy: Double) extends Command
  case object Tick extends Command
  case class AddPlayer(player: Player) extends Command

  def apply(initialWorld: World, speed: Double = 10.0): Behavior[Command] =
    Behaviors.setup { _ =>
      var world = initialWorld
      var directions: Map[String, (Double, Double)] = Map.empty

      Behaviors.receiveMessage:
        case GetWorld(replyTo) =>
          replyTo ! world
          Behaviors.same

        case MovePlayerDirection(id, dx, dy) =>
          directions = directions.updated(id, (dx, dy))
          Behaviors.same

        case Tick =>
          println("Tick received")
          directions.foreach {
            case (id, (dx, dy)) =>
              world.playerById(id).foreach { player =>
                val newPlayerPosition = updatePlayerPosition(player, dx, dy, speed, world)
                world = updateWorldAfterMovement(newPlayerPosition, world)
              }
          }
          Behaviors.same

        case AddPlayer(player) =>
          world.addPlayer(player)
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