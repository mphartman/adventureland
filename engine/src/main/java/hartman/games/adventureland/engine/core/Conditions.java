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

    public static final Condition HAS_EXIT = (pc) -> {
        try {
            Direction desiredExit = Direction.valueOf(pc.getNoun().getName());
            return pc.getGameState().getPlayerCurrentPosition().hasExit(desiredExit);
        } catch (IllegalArgumentException e) {
            return false;
        }
    };

    /**
     * True if the player's current room is ROOM, which must be the name of this class's room.
     */
    public static class IN_ROOM implements Condition {
        private final Room room;

        public IN_ROOM(Room room) {
            this.room = room;
        }

        @Override
        public Boolean apply(PlayerCommand playerCommand) {
            return playerCommand.getGameState().getPlayerCurrentPosition().equals(room);
        }
    }

    /**
     * True if the player is carrying ITEM, which must be the name of an item defined somewhere.
     */
    public static class ITEM_CARRIED implements Condition {

        private final Item item;

        public ITEM_CARRIED(Item item) {
            this.item = item;
        }

        @Override
        public Boolean apply(PlayerCommand playerCommand) {
            return playerCommand.getGameState().getPlayer().hasInInventory(item);
        }
    }

    /**
     * True if ITEM is in the player's current room
     */
    public static class ITEM_HERE implements Condition {

        private final Item item;

        public ITEM_HERE(Item item) {
            this.item = item;
        }

        @Override
        public Boolean apply(PlayerCommand playerCommand) {
            return playerCommand.getGameState().getPlayerCurrentPosition().containsItem(item);
        }
    }

    /**
     * True if ITEM is either being carried by the player
     * or in the player's current room
     * (i.e. if either carried ITEM or here ITEM is true.)
     */
    public static class IS_PRESENT implements Condition {
        private Condition isItemCarried;
        private Condition isItemHere;

        public IS_PRESENT(Item item) {
            isItemCarried = new ITEM_CARRIED(item);
            isItemHere = new ITEM_HERE(item);
        }

        @Override
        public Boolean apply(PlayerCommand playerCommand) {
            return isItemCarried.apply(playerCommand) || isItemHere.apply(playerCommand);
        }
    }

}
