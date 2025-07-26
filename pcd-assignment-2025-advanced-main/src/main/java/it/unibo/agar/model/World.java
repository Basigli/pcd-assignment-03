package it.unibo.agar.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class World implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int width;
    private final int height;
    private List<Player> players;
    private final List<Food> foods;

    public World(int width, int height, List<Player> players, List<Food> foods) {
        this.width = width;
        this.height = height;
        this.players = players;
        this.foods = List.copyOf(foods);     // Ensure immutability
    }

    public World(int width, int height, List<Food> foods) {
        this.width = width;
        this.height = height;
        this.players = new ArrayList<>();
        this.foods = List.copyOf(foods);     // Ensure immutability

    }

    public void addPlayer(Player newPlayer) {
        players.add(newPlayer);
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public List<Player> getPlayersExcludingSelf(final Player player) {
        return players.stream()
                .filter(p -> !p.getId().equals(player.getId()))
                .collect(Collectors.toList());
    }

    public Optional<Player> getPlayerById(final String id) {
        return players.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }


    public World removePlayers(final List<Player> playersToRemove) {
        List<String> idsToRemove = playersToRemove.stream().map(Player::getId).toList();
        List<Player> newPlayers = players.stream()
                .filter(p -> !idsToRemove.contains(p.getId()))
                .collect(Collectors.toList());
        return new World(width, height, newPlayers, foods);
    }

    public World removeFoods(List<Food> foodsToRemove) {
        List<Food> newFoods = foods.stream()
                .filter(f -> !foodsToRemove.contains(f)) // Assumes Food has proper equals/hashCode or relies on object identity if not overridden
                .collect(Collectors.toList());
        return new World(width, height, players, newFoods);
    }

    public void removePlayerById(String playerId) {
        players.removeIf(p -> p.getId().equals(playerId));
    }
}
