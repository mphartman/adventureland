package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Condition;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;

import java.util.Random;
import java.util.function.Supplier;

public final class Conditions {
    private Conditions() {
        throw new IllegalStateException();
    }

    /**
     * Returns true for the given number of times.
     */
    public static class TIMES implements Condition {
        private int counter;
        private final int maxTimes;

        public TIMES(int times) {
            this.maxTimes = times;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            if (counter++ < maxTimes) {
                return true;
            }
            return false;
        }
    }

    /**
     * A condition which returns true based on a desired probability and a random number.
     * E.g. given a probability of 10, this condition should evaluate to true, 10% of the time.
     */
    public static class OCCURS_RANDOMLY implements Condition {
        private Integer probability;
        private Supplier<Integer> randomIntFn;

        public OCCURS_RANDOMLY(Integer probability) {
            this(probability, () -> new Random().nextInt(100) + 1 /* 1 - 100 */);
        }

        public OCCURS_RANDOMLY(Integer probability, Supplier<Integer> randomIntFn) {
            if (probability < 0 || probability > 100) {
                throw new IllegalArgumentException("Invalid value. Probability must be between 0 and 100 inclusive.");
            }
            this.probability = probability;
            this.randomIntFn = randomIntFn;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            if (probability == 0) {
                return false;
            } else if (probability == 100) {
                return true;
            } else {
                return probability - randomIntFn.get() >= 0;
            }
        }
    }

    /**
     * True if player's requested noun represents a valid direction and that the current room
     * she is in has an exit matching that direction.
     */
    public static final Condition HAS_EXIT = (command, gameState) -> gameState.getCurrentRoom().hasExit(command.getNoun());

    /**
     * True if the player's current room is ROOM.
     */
    public static class IN_ROOM implements Condition {
        private final Room room;

        public IN_ROOM(Room room) {
            this.room = room;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return gameState.getCurrentRoom().equals(room);
        }
    }

    /**
     * True if the player is carrying ITEM in their inventory.
     */
    public static class ITEM_CARRIED implements Condition {

        private final Item item;

        public ITEM_CARRIED(Item item) {
            this.item = item;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return item.isCarried();
        }
    }

    /**
     * True if ITEM is in the player's current room.
     */
    public static class ITEM_HERE implements Condition {

        private final Item item;

        public ITEM_HERE(Item item) {
            this.item = item;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return item.isHere(gameState.getCurrentRoom());
        }
    }

    /**
     * True if ITEM is either being carried by the player
     * or is in the player's current room.
     */
    public static class IS_PRESENT implements Condition {
        private Condition isItemCarried;
        private Condition isItemHere;

        public IS_PRESENT(Item item) {
            isItemCarried = new ITEM_CARRIED(item);
            isItemHere = new ITEM_HERE(item);
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return isItemCarried.matches(command, gameState) || isItemHere.matches(command, gameState);
        }
    }

    /**
     * Returns the inverse of the wrapped condition.
     */
    public static class NOT implements Condition {
        private final Condition operand;

        public NOT(Condition operand) {
            this.operand = operand;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return !operand.matches(command, gameState);
        }
    }

    /**
     * True if ITEM has moved from its original starting location.
     */
    public static class ITEM_MOVED implements Condition {
        private final Item item;

        public ITEM_MOVED(Item item) {
            this.item = item;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return item.hasMoved();
        }
    }
}
