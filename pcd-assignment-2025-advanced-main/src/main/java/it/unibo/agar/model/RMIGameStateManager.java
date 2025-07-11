package it.unibo.agar.model;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class RMIGameStateManager implements RemoteGameStateManager {

    private static final double PLAYER_SPEED = 2.0;
    private static final int MAX_FOOD_ITEMS = 150;
    private static final Random random = new Random();
    private World world;
    private final Map<String, Position> playerDirections;


    public RMIGameStateManager(World initialWorld) throws RemoteException {
        super();
        this.world = initialWorld;
        this.playerDirections = new HashMap<>();
        // this.world.getPlayers().forEach(p -> playerDirections.put(p.getId(), Position.ZERO));
    }

    @Override
    public World getWorld() throws RemoteException {
        return this.world;
    }

    @Override
    public void setPlayerDirection(String playerId, double dx, double dy) throws RemoteException {
        // Ensure player exists before setting direction
        if (world.getPlayerById(playerId).isPresent()) {
            this.playerDirections.put(playerId, Position.of(dx, dy));
        }
    }

    @Override
    public void tick() throws RemoteException {
        this.world = handleEating(moveAllPlayers(this.world));
        cleanupPlayerDirections();
    }

    @Override
    public void addPlayer(Player player) throws RemoteException {
        this.world.addPlayer(player);
    }


    private World moveAllPlayers(final World currentWorld) {
        final List<Player> updatedPlayers = currentWorld.getPlayers().stream()
                .map(player -> {
                    Position direction = playerDirections.getOrDefault(player.getId(), Position.ZERO);
                    final double newX = player.getX() + direction.x() * PLAYER_SPEED;
                    final double newY = player.getY() + direction.y() * PLAYER_SPEED;
                    return player.moveTo(newX, newY);
                })
                .collect(Collectors.toList());

        return new World(currentWorld.getWidth(), currentWorld.getHeight(), updatedPlayers, currentWorld.getFoods());
    }

    private World handleEating(final World currentWorld) {
        final List<Player> updatedPlayers = currentWorld.getPlayers().stream()
                .map(player -> growPlayer(currentWorld, player))
                .toList();

        final List<Food> foodsToRemove = currentWorld.getPlayers().stream()
                .flatMap(player -> eatenFoods(currentWorld, player).stream())
                .distinct()
                .toList();

        final List<Player> playersToRemove = currentWorld.getPlayers().stream()
                .flatMap(player -> eatenPlayers(currentWorld, player).stream())
                .distinct()
                .toList();

        return new World(currentWorld.getWidth(), currentWorld.getHeight(), updatedPlayers, currentWorld.getFoods())
                .removeFoods(foodsToRemove)
                .removePlayers(playersToRemove);
    }

    private Player growPlayer(final World world, final Player player) {
        final Player afterFood = eatenFoods(world, player).stream()
                .reduce(player, Player::grow, (p1, p2) -> p1);

        return eatenPlayers(world, afterFood).stream()
                .reduce(afterFood, Player::grow, (p1, p2) -> p1);
    }

    private List<Food> eatenFoods(final World world, final Player player) {
        return world.getFoods().stream()
                .filter(food -> EatingManager.canEatFood(player, food))
                .toList();
    }

    private List<Player> eatenPlayers(final World world, final Player player) {
        return world.getPlayersExcludingSelf(player).stream()
                .filter(other -> EatingManager.canEatPlayer(player, other))
                .toList();
    }

    private void cleanupPlayerDirections() {
        List<String> currentPlayerIds = this.world.getPlayers().stream()
                .map(Player::getId)
                .collect(Collectors.toList());

        this.playerDirections.keySet().retainAll(currentPlayerIds);
        this.world.getPlayers().forEach(p ->
                playerDirections.putIfAbsent(p.getId(), Position.ZERO));
    }
}
