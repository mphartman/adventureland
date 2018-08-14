package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Condition;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Word;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.stream;

public final class Conditions {

    private Conditions() {
        throw new IllegalStateException("utility class");
    }

    /**
     * Performs a logical NOT of the Condition operand.
     */
    public static Condition not(Condition operand) {
        return (command, gameState) -> !operand.matches(command, gameState);
    }

    /**
     * Two Conditions combined with a 'logical OR'
     */
    public static Condition or(Condition c1, Condition c2) {
        return (command, gameState) -> c1.matches(command, gameState) || c2.matches(command, gameState);
    }

    /**
     * Two Conditions combined with a 'logical AND'
     */
    public static Condition and(Condition c1, Condition c2) {
        return ((command, gameState) -> c1.matches(command, gameState) && c2.matches(command, gameState));
    }

    /**
     * TRUE if word matches Command first word
     */
    public static Condition firstWordMatches(Word word) {
        return ((command, gameState) -> word.matches(command.getFirstWord()));
    }

    /**
     * TRUE if Command first word matches any of the given words.
     */
    public static Condition anyMatchesFirstWord(Word... words) {
        return (command, gameState) -> stream(words).anyMatch(verb -> verb.matches(command.getFirstWord()));
    }

    /**
     * TRUE if word matches Command second word
     */
    public static Condition secondWordMatches(Word word) {
        return ((command, gameState) -> word.matches(command.getSecondWord()));
    }

    /**
     * TRUE if Command second word matches any of the given words.
     */
    public static Condition anyMatchesSecondWord(Word... words) {
        return (command, gameState) -> stream(words).anyMatch(noun -> noun.matches(command.getSecondWord()));
    }

    /**
     * True if player's requested noun represents a valid direction and that the current room
     * she is in has an exit matching that direction.
     */
    public static final Condition roomHasExit = (command, gameState) -> gameState.getCurrentRoom().hasExit(command.getSecondThenFirst());

    /**
     * True if the player's current room is ROOM.
     */
    public static Condition in(Room room) {
        return (command, gameState) -> gameState.getCurrentRoom().equals(room);
    }

    /**
     * True if the player is carrying ITEM in their inventory.
     */
    public static Condition carrying(Item item) {
        return (command, gameState) -> item.isCarried();
    }

    /**
     * True if ITEM is in the player's current room.
     */
    public static Condition here(Item item) {
        return (command, gameState) -> item.isHere(gameState.getCurrentRoom());
    }

    /**
     * True if ITEM is either being carried by the player
     * or is in the player's current room.
     */
    public static Condition present(Item item) {
        return or(here(item), carrying(item));
    }

    /**
     * A condition which returns true based on a desired probability and a random number.
     * E.g. given a probability of 10, this condition should evaluate to true, 10% of the time.
     */
    public static Condition random(Integer probability) {
        return random(probability, () -> new java.util.Random().nextInt(100) /* 0 - 99 */);
    }

    /**
     * A condition which returns true based on a desired probability and the result of the given supplier function.
     */
    public static Condition random(Integer probability, Supplier<Integer> d100) {
        if (probability < 0 || probability > 100) {
            throw new IllegalArgumentException("Invalid value. Probability must be between 0 and 100 inclusive.");
        }
        return (command, gameState) -> probability - d100.get() > 0;
    }

    /**
     * True if ITEM has moved from its original starting location.
     */
    public static Condition hasMoved(Item item) {
        return (command, gameState) -> item.hasMoved();
    }

    /**
     * Returns true for the given number of times.
     */
    public static Condition times(int times) {
        return new Condition() {

            private int counter;

            @Override
            public boolean matches(Command command, GameState gameState) {
                return counter++ < times;
            }
        };
    }

    /**
     * True if ITEM is in the ROOM
     */
    public static Condition there(Item item, Room room) {
        return (command, gameState) -> item.isHere(room);
    }

    /**
     * True if ITEM is somewhere in the game, i.e. not "nowhere"
     */
    public static Condition exists(Item item) {
        return (command, gameState) -> gameState.exists(item);
    }

    /**
     * True if flag is set to true.
     */
    public static Condition isFlagSet(String name) {
        return (command, gameState) -> gameState.getFlag(name);
    }

    /**
     * Returns result of evaluating value of counter using given compare function.
     */
    public static Condition compareCounter(String name, Function<Integer, Boolean> compare) {
        return ((command, gameState) -> compare.apply(gameState.getCounter(name)));
    }

    /**
     * True if named string equals given value.
     */
    public static Condition stringEquals(String name, String value) {
        return (command, gameState) -> gameState.getString(name).equals(value);
    }
}
