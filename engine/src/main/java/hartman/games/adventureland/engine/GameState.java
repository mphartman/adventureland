package hartman.games.adventureland.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * The state of the game world which consists of the player's current position
 * and a set of flags which the adventure's actions may set and interpret.
 */
public class GameState {
    private final Player player;
    private final Map<String, Object> flags = new HashMap<>();

    private Room playerCurrentPosition;

    public GameState(Player player, Room startRoom) {
        this.player = player;
        this.playerCurrentPosition = startRoom;
    }

    public Player getPlayer() {
        return player;
    }

    public Room getPlayerCurrentPosition() {
        return playerCurrentPosition;
    }

    public Room exitTowards(Noun direction) {
        Room nextRoom = playerCurrentPosition.exit(direction);
        return movePlayerTo(nextRoom);
    }

    public Room movePlayerTo(Room room) {
        Room previousRoom = playerCurrentPosition;
        playerCurrentPosition = room;
        return previousRoom;
    }

    public void setFlag(String key, Object value) {
        flags.put(key, value);
    }

    public Object getFlag(String key) {
        return flags.get(key);
    }
}
