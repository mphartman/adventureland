package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Result;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Word;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Results {

    private Results() {
        throw new IllegalStateException("utility class");
    }

    /**
     * Indicate that game state is no longer running.
     */
    public static final Result quit = (command, gameState, display) -> gameState.quit();

    /**
     * Change the current toom based on the given direction.
     * @throws IllegalStateException if direction is not an exit from the current room.
     */
    public static Result go(Word direction) {
        return (command, gameState, display) -> gameState.exitTowards(direction);
    }

    public static Result goInDirectionMatchingCommandWordAt(int position) {
        return (command, gameState, display) -> go(command.getWordOrNone(position)).execute(command, gameState, display);
    }

    /**
     * Asks gamestate to describe itself
     */
    public static final Result look = (command, gameState, display) -> gameState.describe(display);

    private static Pattern wordPlaceholderPattern = Pattern.compile("\\{word:(.+?)\\}");
    private static Pattern counterPlaceholderPattern = Pattern.compile("\\{counter:(.+?)\\}");
    private static Pattern flagPlaceholderPattern = Pattern.compile("\\{flag:(.+?)\\}");
    private static Pattern stringPlaceholderPattern = Pattern.compile("\\{string:(.+?)\\}");

    private static String resolvePlaceholder(String s, Pattern pattern, Function<String, String> resolver) {
        Matcher matcher = pattern.matcher(s);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String group = (matcher.groupCount() == 0) ? matcher.group(0) : matcher.group(1);
            String result = resolver.apply(group);
            builder.append(s, i, matcher.start());
            builder.append(result);
            i = matcher.end();
        }
        builder.append(s.substring(i));
        return builder.toString();
    }

    /**
     * Prints the specified message to the {@link Display}
     */
    public static Result print(String message) {
        return (command, gameState, display) -> {
            String output = message;
            output = resolvePlaceholder(output, wordPlaceholderPattern, pos -> command.getWord(Integer.parseInt(pos)).orElse(new Word("")).getName());
            output = resolvePlaceholder(output, counterPlaceholderPattern, counter -> String.valueOf(gameState.getCounter(counter)));
            output = resolvePlaceholder(output, flagPlaceholderPattern, flag -> String.valueOf(gameState.getFlag(flag)));
            output = resolvePlaceholder(output, stringPlaceholderPattern, gameState::getString);
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
     * Displays a list of items that the player is carrying.
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
     * Put ITEM in Inventory.
     */
    public static Result get(Item item) {
        return (command, gameState, display) -> gameState.putInInventory(item);
    }

    /**
     * Drop ITEM in the current room.
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
