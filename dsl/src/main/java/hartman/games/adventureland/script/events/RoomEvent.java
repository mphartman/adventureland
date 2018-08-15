package hartman.games.adventureland.script.events;

import java.util.Map;

/**
 * A RoomEvent provides access to information about Room definitions
 */
public interface RoomEvent extends ScriptEvent {

    String getName();

    String getDescription();

    Map<String, String> getExits();

}
