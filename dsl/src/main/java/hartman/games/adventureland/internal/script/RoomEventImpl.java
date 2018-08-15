package hartman.games.adventureland.internal.script;

import hartman.games.adventureland.script.events.RoomEvent;

import java.util.HashMap;
import java.util.Map;

public class RoomEventImpl implements RoomEvent {
    private String name;
    private String description;
    private Map<String, String> exits = new HashMap<>();

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExit(String direction, String targetRoomName) {
        exits.put(direction, targetRoomName);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Map<String, String> getExits() {
        return exits;
    }
}
