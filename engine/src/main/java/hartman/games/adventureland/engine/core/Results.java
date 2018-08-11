package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Result;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.GameElementVisitor;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Results {

    public static final Result quit = (command, gameState, display) -> gameState.quit();

    public static final Result go = (command, gameState, display) -> gameState.exitTowards(command.getNoun());

    public static final Result goUsingVerb = (command, gameState, display) -> gameState.exitTowards(new Noun(command.getVerb().getName()));

    public static Result look(Look.LookCallback callback) {
        return new Look(callback);
    }

    public static Result print(String message) {
        return new Print(message);
    }

    public static Result printf(String message, Object... args) {
        return print(String.format(message, args));
    }

    public static Result println(String message) {
        return printf(message + "%n");
    }

    public static Result inventory(Inventory.InventoryCallback callback) {
        return new Inventory(callback);
    }

    public static Result swap(Item item1, Item item2) {
        return new Swap(item1, item2);
    }

    public static Result gotoRoom(Room room) {
        return new Goto(room);
    }

    public static Result put(Item item, Room room) {
        return ((command, gameState, display) -> item.drop(room));
    }

    public static Result putHere(Item item) {
        return (command, gameState, display) -> item.drop(gameState.getCurrentRoom());
    }

    public static final Result get = ((command, gameState, display) -> gameState.putInInventory(command.getNoun()));

    public static final Result drop = ((command, gameState, display) -> gameState.drop(command.getNoun()));

    public static Result putWith(Item item1, Item item2) {
        return new PutWith(item1, item2);
    }

    public static Result destroy(Item item) {
        return new Destroy(item);
    }

    /**
     * Provides the room, exits, and items to a callback.
     */
    public static class Look implements Result {

        @FunctionalInterface
        public interface LookCallback {
            String execute(Room room, List<Room.Exit> exits, List<Item> items);
        }

        private final LookCallback callback;

        Look(LookCallback callback) {
            this.callback = callback;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            List<Room> rooms = new ArrayList<>();
            List<Room.Exit> exits = new ArrayList<>();
            List<Item> items = new ArrayList<>();

            gameState.describe(new GameElementVisitor() {
                @Override
                public void visit(Item item) {
                    items.add(item);
                }

                @Override
                public void visit(Room room) {
                    rooms.add(room);
                }

                @Override
                public void visit(Room.Exit exit) {
                    exits.add(exit);
                }
            });

            String description = callback.execute(
                    rooms.stream().findFirst().orElse(Room.NOWHERE),
                    Collections.unmodifiableList(exits),
                    Collections.unmodifiableList(items));

            display.print(description);
        }
    }

    /**
     * Prints the specified message to the {@link Display}
     */
    public static class Print implements Result {

        private final String message;

        Print(String message) {
            this.message = message;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            String output = message.replaceAll("\\Q{noun}\\E", command.getNoun().getName());
            output = output.replaceAll("\\Q{verb}\\E", command.getVerb().getName());
            display.print(output);
        }
    }

    /**
     * Provides a callback with a list of items that the player carrying.
     */
    public static class Inventory implements Result {

        @FunctionalInterface
        public interface InventoryCallback {
            String execute(List<Item> items);
        }

        private final InventoryCallback callback;

        Inventory(InventoryCallback callback) {
            this.callback = callback;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            List<Item> items = new ArrayList<>();

            gameState.inventory(new GameElementVisitor() {
                @Override
                public void visit(Item item) {
                    items.add(item);
                }

                @Override
                public void visit(Room room) {
                    // do nothing
                }

                @Override
                public void visit(Room.Exit exit) {
                    // do nothing
                }
            });

            display.print(callback.execute(items));
        }
    }

    /**
     * Exchanges the two specified items, so that each occupies the location previously occupied by the other.
     */
    public static class Swap implements Result {

        private final Item item1;
        private final Item item2;

        Swap(Item item1, Item item2) {
            this.item1 = item1;
            this.item2 = item2;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            item1.drop(item2.drop(item1.drop(Room.NOWHERE)));
        }
    }

    /**
     * Moves to the specified room
     */
    public static class Goto implements Result {

        private final Room room;

        Goto(Room room) {
            this.room = room;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            gameState.moveTo(room);
        }
    }

    /**
     * Puts the first-specified item into the same location as the second.
     */
    public static class PutWith implements Result {

        private final Item item1;
        private final Item item2;

        public PutWith(Item item1, Item item2) {
            this.item1 = item1;
            this.item2 = item2;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            this.item1.putWith(item2);
        }
    }

    /**
     * Removes ITEM from the game.
     */
    public static class Destroy implements Result {
        private final Item item;

        public Destroy(Item item) {
            this.item = item;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            gameState.destroy(item);
        }
    }
}
