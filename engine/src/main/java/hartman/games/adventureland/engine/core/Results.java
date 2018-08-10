package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Result;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.GameElementVisitor;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Results {
    private Results() {
        throw new IllegalStateException();
    }

    public static final Result quit = (command, gameState, display) -> gameState.quit();

    public static final Result go = (command, gameState, display) -> gameState.exitTowards(command.getNoun());

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
        return new Put(item, room);
    }

    public static final Result get = ((command, gameState, display) -> gameState.get(command.getNoun()));

    public static final Result drop = ((command, gameState, display) -> gameState.drop(command.getNoun()));

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

    public static class Print implements Result {

        private final String message;

        Print(String message) {
            this.message = message;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            display.print(message);
        }
    }

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

    public static class Put implements Result {

        private final Room room;
        private final Item item;

        Put(Item item, Room room) {
            this.room = room;
            this.item = item;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            item.drop(room);
        }
    }
}
