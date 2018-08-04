package hartman.games.adventureland.engine;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Avatar of the person playing the game.
 */
public class Player {
    private final String name;
    private final Set<Item> inventory = new LinkedHashSet<>();

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<Item> getInventory() {
        return Collections.unmodifiableSet(inventory);
    }

    public boolean hasInInventory(Item item) {
        return inventory.contains(item);
    }

    public boolean addToInventory(Item item) {
        return inventory.add(item);
    }

    public boolean removeFromInventory(Item item) {
        return inventory.remove(item);
    }
}
