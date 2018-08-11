package hartman.games.adventureland.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The state of the game world which consists of the player's current position
 * and a toSet of flags which the adventure's actions may toSet and interpret.
 */
public class GameState {
    private final Map<String, Object> flags = new HashMap<>();
    private final Set<Item> items = new LinkedHashSet<>();
    private boolean running;
    private Room currentRoom;

    public GameState(Room startingRoom, Set<Item> items) {
        this.currentRoom = startingRoom;
        this.items.addAll(items);
        this.running = true;
    }

    public GameState(Room startRoom) {
        this(startRoom, Collections.emptySet());
    }

    public void setFlag(String key, Object value) {
        flags.put(key, value);
    }

    public Object getFlag(String key) {
        return flags.get(key);
    }

    public boolean isRunning() {
        return running;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Room exitTowards(Noun direction) {
        Room nextRoom = currentRoom.exit(direction);
        return moveTo(nextRoom);
    }

    public Room moveTo(Room room) {
        Room previousRoom = currentRoom;
        currentRoom = room;
        return previousRoom;
    }

    public void quit() {
        running = false;
    }

    /**
     * Places item represented here as a Noun, in the inventory.
     * Item only needs to exist and be portable, it does not need to be in the current room.
     */
    public void putInInventory(Noun noun) {
        items.stream()
                .filter(i -> i.equals(noun))
                .filter(Item::isPortable)
                .findFirst()
                .ifPresent(Item::stow);
    }

    /**
     * Visits the current room and the items in that room.
     */
    public void describe(GameElementVisitor visitor) {
        currentRoom.accept(visitor);
        items.stream()
                .filter(item -> item.isHere(currentRoom))
                .forEach(item -> item.accept(visitor));
    }

    /**
     * Visits only those items currently held in player's inventory.
     */
    public void inventory(GameElementVisitor visitor) {
        items.stream()
                .filter(Item::isCarried)
                .forEach(item -> item.accept(visitor));
    }

    /**
     * Places the item in the current room.
     */
    public void drop(Noun noun) {
        items.stream()
                .filter(item -> item.equals(noun))
                .findFirst()
                .ifPresent(item -> item.drop(currentRoom));
    }

    /**
     * Removes item thus effectively destroying it from game.
     */
    public void destroy(Noun noun) {
         Optional<Item> maybeItem = items.stream()
                 .filter(item -> item.equals(noun))
                 .findFirst();
         maybeItem.ifPresent(item -> {
             items.remove(item);
             item.destroy();
         });
    }

    /**
     * True if ITEM is in the game and not destroyed.
     */
    public boolean exists(Noun noun) {
        return items.stream().anyMatch(item -> item.equals(noun) && !item.isDestroyed());
    }

}
