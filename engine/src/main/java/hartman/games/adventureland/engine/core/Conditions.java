package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Condition;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;

import java.util.function.Supplier;

public final class Conditions {
    private Conditions() {
        throw new IllegalStateException();
    }

    /**
     * Returns true for the given number of times.
     */
    public static class Times implements Condition {
        private int counter;
        private final int maxTimes;

        public Times(int times) {
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
    public static class Random implements Condition {

        public static Random of(Integer probability) {
            return new Random(probability);
        }

        private Integer probability;
        private Supplier<Integer> randomIntFn;

        public Random(Integer probability) {
            this(probability, () -> new java.util.Random().nextInt(100) + 1 /* 1 - 100 */);
        }

        public Random(Integer probability, Supplier<Integer> randomIntFn) {
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
    public static final Condition HasExit = (command, gameState) -> gameState.getCurrentRoom().hasExit(command.getNoun());

    /**
     * True if the player's current room is ROOM.
     */
    public static class InRoom implements Condition {

        public static InRoom of(Room room) {
            return new InRoom(room);
        }

        private final Room room;

        public InRoom(Room room) {
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
    public static class ItemCarried implements Condition {

        public static ItemCarried of(Item item) {
            return new ItemCarried(item);
        }

        private final Item item;

        public ItemCarried(Item item) {
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
    public static class ItemHere implements Condition {

        public static ItemHere of(Item item) {
            return new ItemHere(item);
        }

        private final Item item;

        public ItemHere(Item item) {
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
    public static class IsPresent implements Condition {

        public static IsPresent of(Item item) {
            return new IsPresent(item);
        }

        private Condition isItemCarried;
        private Condition isItemHere;

        public IsPresent(Item item) {
            isItemCarried = new ItemCarried(item);
            isItemHere = new ItemHere(item);
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return isItemCarried.matches(command, gameState) || isItemHere.matches(command, gameState);
        }
    }

    /**
     * Returns the inverse of the wrapped condition.
     */
    public static class Not implements Condition {

        public static Not of(Condition operand) {
            return new Not(operand);
        }

        private final Condition operand;

        public Not(Condition operand) {
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
    public static class ItemMoved implements Condition {
        private final Item item;

        public ItemMoved(Item item) {
            this.item = item;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return item.hasMoved();
        }
    }
}
