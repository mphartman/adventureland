package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.*;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static hartman.games.adventureland.engine.Action.*;

public final class Conditions {
    private Conditions() {
        throw new IllegalStateException();
    }

    public static Set<Condition> asSet(Condition... conditions) {
        return new LinkedHashSet<>(Arrays.asList(conditions));
    }

    /**
     * True if player's requested noun represents a valid direction and that the current room
     * she is in has an exit matching that direction.
     */
    public static final Condition HAS_EXIT = (playerCommand, gameState) -> {
        return gameState.getPlayerCurrentPosition().hasExit(playerCommand.getNoun());
    };

    /**
     * True if the player's current room is ROOM.
     */
    public static class IN_ROOM implements Condition {
        private final Room room;

        public IN_ROOM(Room room) {
            this.room = room;
        }

        @Override
        public Boolean apply(PlayerCommand playerCommand, GameState gameState) {
            return gameState.getPlayerCurrentPosition().equals(room);
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
        public Boolean apply(PlayerCommand playerCommand, GameState gameState) {
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
        public Boolean apply(PlayerCommand playerCommand, GameState gameState) {
            return item.isHere(gameState.getPlayerCurrentPosition());
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
        public Boolean apply(PlayerCommand playerCommand, GameState gameState) {
            return isItemCarried.apply(playerCommand, gameState) || isItemHere.apply(playerCommand, gameState);
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
        public Boolean apply(PlayerCommand playerCommand, GameState gameState) {
            return !operand.apply(playerCommand, gameState);
        }
    }
}
