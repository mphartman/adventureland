package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Result;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;

public final class Results {

    private Results() {
        throw new IllegalStateException("utility class");
    }

    /**
     * Indicate that game state is no longer running.
     */
    public static final Result quit = (command, gameState, display) -> gameState.quit();

    /**
     * Change current room based on the given Command's second word if it's not NONE, otherwise the first word
     */
    public static final Result go = (command, gameState, display) -> gameState.exitTowards(command.getSecondThenFirst());

    /**
     * Asks gamestate to describe itself
     */
    public static final Result look = (command, gameState, display) -> gameState.describe(display);

    /**
     * Prints the specified message to the {@link Display}
     */
    public static Result print(String message) {
        return (command, gameState, display) -> {
            String output = message.replaceAll("\\Q{noun}\\E", command.getSecondWord().getName());
            output = output.replaceAll("\\Q{verb}\\E", command.getFirstWord().getName());
            display.print(output);
        };
    }

    /**
     * Message is passed to String.format
     *
     * @see #print(String)
     */
    public static Result printf(String message, Object... args) {
        return print(String.format(message, args));
    }

    /**
     * Message has a newline appended.
     *
     * @see #printf(String, Object...)
     */
    public static Result println(String message) {
        return printf(message.concat("%n"));
    }

    /**
     * Provides a callback with a list of items that the player carrying.
     */
    public static final Result inventory = (command, gameState, display) -> gameState.inventory(display);

    /**
     * Exchanges the two specified items, so that each occupies the location previously occupied by the other.
     */
    public static Result swap(Item item1, Item item2) {
        return (command, gameState, display) -> item1.drop(item2.drop(item1.drop(Room.NOWHERE)));
    }

    /**
     * Moves to the specified room
     */
    public static Result gotoRoom(Room room) {
        return (command, gameState, display) -> gameState.moveTo(room);
    }

    /**
     * Put ITEM in ROOM.
     */
    public static Result put(Item item, Room room) {
        return (command, gameState, display) -> item.drop(room);
    }

    /**
     * Put ITEM in current room.
     */
    public static Result putHere(Item item) {
        return (command, gameState, display) -> item.drop(gameState.getCurrentRoom());
    }

    /**
     * Put item mentioned in Command Noun in Inventory.
     */
    public static final Result get = ((command, gameState, display) -> gameState.putInInventory(command.getSecondWord()));

    /**
     * Drop item mentioned in Command Noun in current room.
     */
    public static final Result drop = ((command, gameState, display) -> gameState.drop(command.getSecondWord()));

    /**
     * Drop item in current room.
     */
    public static Result drop(Item item) {
        return (command, gameState, display) -> gameState.drop(item);
    }

    /**
     * Puts the first-specified item into the same location as the second.
     */
    public static Result putWith(Item item1, Item item2) {
        return (command, gameState, display) -> item1.putWith(item2);
    }

    /**
     * Removes ITEM from the game.
     */
    public static Result destroy(Item item) {
        return (command, gameState, display) -> gameState.destroy(item);
    }

    /**
     * Sets the specified flag to TRUE or FALSE.
     */
    public static Result setFlag(String name, Boolean value) {
        return (command, gameState, display) -> gameState.setFlag(name, value);
    }

    /**
     * Resets the specified flag to FALSE
     */
    public static Result resetFlag(String name) {
        return (command, gameState, display) -> gameState.resetFlag(name);
    }

    /**
     * Sets named counter to integer VALUE
     */
    public static Result setCounter(String name, Integer value) {
       return (command, gameState, display) -> gameState.setCounter(name, value);
    }

    /**
     * Increments named counter by 1
     */
    public static Result incrementCounter(String name) {
        return (command, gameState, display) -> gameState.setCounter(name, gameState.getCounter(name) + 1);
    }

    /**
     * Decrements named counter by 1
     */
    public static Result decrementCounter(String name) {
        return (command, gameState, display) -> gameState.setCounter(name, gameState.getCounter(name) - 1);
    }

    /**
     * Resets named counter to ZERO.
     */
    public static Result resetCounter(String name) {
        return (command, gameState, display) -> gameState.resetCounter(name);
    }

    /**
     * Sets named string to value
     */
    public static Result setString(String name, String value) {
        return (command, gameState, display) -> gameState.setString(name, value);
    }
}
