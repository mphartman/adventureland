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
     * True if player's requested noun represents a valid direction and that the current room
     * she is in has an exit matching that direction.
     */
    public static final Condition currentRoomHasExit = (command, gameState) -> gameState.getCurrentRoom().hasExit(command.getNoun());

    public static Condition isInRoom(Room room) {
        return new IsInRoom(room);
    }

    public static Condition isItemCarried(Item item) {
        return new IsItemCarried(item);
    }

    public static Condition isItemHere(Item item) {
        return new IsItemHere(item);
    }

    public static Condition isPresent(Item item) {
        return new IsPresent(item);
    }

    public static Condition not(Condition operand) {
        return new Not(operand);
    }

    public static Condition random(Integer probability) {
        return new Random(probability);
    }

    public static Condition hasItemMoved(Item item) {
        return new HasItemMoved(item);
    }

    public static Condition times(int times) {
        return new Times(times);
    }

    public static Condition isItemInRoom(Item item, Room room) {
        return new IsItemInRoom(item, room);
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
     * True if the player's current room is ROOM.
     */
    public static class IsInRoom implements Condition {

        private final Room room;

        public IsInRoom(Room room) {
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
    public static class IsItemCarried implements Condition {

        private final Item item;

        public IsItemCarried(Item item) {
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
    public static class IsItemHere implements Condition {

        private final Item item;

        public IsItemHere(Item item) {
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

        private Condition isItemCarried;
        private Condition isItemHere;

        public IsPresent(Item item) {
            isItemCarried = new IsItemCarried(item);
            isItemHere = new IsItemHere(item);
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
    public static class HasItemMoved implements Condition {
        private final Item item;

        public HasItemMoved(Item item) {
            this.item = item;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return item.hasMoved();
        }
    }

    /**
     * True if ITEM is in the ROOM
     */
    public static class IsItemInRoom implements Condition {
        private final Item item;
        private final Room room;

        public IsItemInRoom(Item item, Room room) {
            this.item = item;
            this.room = room;
        }

        @Override
        public boolean matches(Command command, GameState gameState) {
            return item.isHere(room);
        }
    }
}
