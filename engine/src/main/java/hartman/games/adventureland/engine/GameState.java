package hartman.games.adventureland.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * The state of the game world which consists of the player's current position
 * and a set of flags which the adventure's actions may set and interpret.
 */
public class GameState {
    private final Map<String, Object> flags = new HashMap<>();

    private boolean running;
    private Room currentRoom;

    public GameState(Room startRoom) {
        this.currentRoom = startRoom;
        this.running = true;
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

    public void setFlag(String key, Object value) {
        flags.put(key, value);
    }

    public Object getFlag(String key) {
        return flags.get(key);
    }
}
