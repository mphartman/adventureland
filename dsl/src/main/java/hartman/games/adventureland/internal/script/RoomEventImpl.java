package hartman.games.adventureland.internal.script;

import hartman.games.adventureland.script.events.RoomEvent;

public class RoomEventImpl implements RoomEvent {
    private String name;
    private String description;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
