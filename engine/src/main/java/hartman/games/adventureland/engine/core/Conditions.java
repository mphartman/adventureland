package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Condition;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Room;

import java.util.function.Supplier;

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
    public static Condition or(Condition condition1, Condition condition2) {
        return (command, gameState) -> condition1.matches(command, gameState) || condition2.matches(command, gameState);
    }

    /**
     * True if player's requested noun represents a valid direction and that the current room
     * she is in has an exit matching that direction.
     */
    public static final Condition roomHasExit = (command, gameState) -> gameState.getCurrentRoom().hasExit(command.getNoun());

    public static final Condition currentRoomHasExitByVerb = (command, gameState) -> gameState.getCurrentRoom().hasExit(new Noun(command.getVerb().getName()));

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
     * True if ITEM is somewhere in the game, i.e. not destroyed.
     *
     * An ITEM in NOWHERE is considered to exist.
     */
    public static Condition exists(Item item) {
        return (command, gameState) -> gameState.exists(item);
    }

}
