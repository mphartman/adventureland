package hartman.games.adventureland.engine;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

/**
 * The state of the game world which consists of the player's current position
 * and a toSet of flags which the adventure's actions may toSet and interpret.
 */
public class GameState {
    private final Map<String, Boolean> flags = new HashMap<>();
    private final Map<String, Integer> counters = new HashMap<>();
    private final Map<String, String> strings = new HashMap<>();
    private final Set<Item> items = new LinkedHashSet<>();
    private boolean running;
    private Room currentRoom;

    public GameState(Room startingRoom, Set<Item> items) {
        this.currentRoom = startingRoom;
        this.items.addAll(items);
        this.running = true;
    }

    public GameState(Room startRoom) {
        this(startRoom, emptySet());
    }

    public void setFlag(String name, boolean value) {
        flags.put(name, value);
    }

    public void setFlag(String name) {
        flags.put(name, Boolean.TRUE);
    }

    public boolean getFlag(String name) {
        return flags.getOrDefault(name, Boolean.FALSE);
    }

    public void resetFlag(String name) {
        flags.remove(name);
    }

    public void setCounter(String name, int value) {
        counters.put(name, value);
    }

    public int getCounter(String name) {
        return counters.getOrDefault(name, 0);
    }

    public void resetCounter(String name) {
        counters.remove(name);
    }

    public String getString(String name) {
        return strings.getOrDefault(name, "");
    }

    public void setString(String name, String value) {
        strings.put(name, value);
    }

    public boolean isRunning() {
        return running;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Room exitTowards(Word direction) {
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
    public void putInInventory(Word word) {
        items.stream()
                .filter(i -> i.matches(word))
                .filter(Item::isPortable)
                .findFirst()
                .ifPresent(Item::stow);
    }

    /**
     * Visits the current room and the items in that room.
     */
    public void describe(Display display) {
        display.look(currentRoom, unmodifiableSet(items.stream()
                .filter(item -> item.isHere(currentRoom))
                .collect(toSet())));
    }

    /**
     * Visits only those items currently held in player's inventory.
     */
    public void inventory(Display display) {
        display.inventory(unmodifiableSet(items.stream()
                .filter(Item::isCarried)
                .collect(toSet())));
    }

    /**
     * Places the item in the current room.
     */
    public void drop(Word word) {
        items.stream()
                .filter(item -> item.matches(word))
                .findFirst()
                .ifPresent(item -> item.drop(currentRoom));
    }

    /**
     * Removes item thus effectively destroying it from game.
     */
    public void destroy(Word word) {
        items.stream()
                .filter(item -> item.matches(word))
                .findFirst()
                .ifPresent(Item::destroy);
    }

    /**
     * True if ITEM is in the game and not destroyed.
     */
    public boolean exists(Word word) {
        return items.stream()
                .anyMatch(item -> item.matches(word) && !item.isDestroyed());
    }

}
