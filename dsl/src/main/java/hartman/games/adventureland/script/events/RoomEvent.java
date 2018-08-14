package hartman.games.adventureland.script.events;

/**
 * A RoomEvent provides access to information about Room definitions
 */
public interface RoomEvent extends ScriptEvent {

    String getName();

    String getDescription();

}
