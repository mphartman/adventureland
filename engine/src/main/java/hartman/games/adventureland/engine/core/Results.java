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

    public static final Result Quit = (command, gameState, display) -> gameState.quit();

    public static final Result Go = (command, gameState, display) -> gameState.exitTowards(command.getNoun());

    public static class Look implements Result {

        public static Look of(LookCallback callback) {
            return new Look(callback);
        }

        @FunctionalInterface
        public interface LookCallback {
            String execute(Room room, List<Room.Exit> exits, List<Item> items);
        }

        private final LookCallback callback;

        public Look(LookCallback callback) {
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

        public static Print of(String message) {
            return new Print(message);
        }

        private final String message;

        public Print(String message) {
            this.message = message;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            display.print(message);
        }
    }

    public static final Result Get = ((command, gameState, display) -> gameState.get(command.getNoun()));

    public static class Inventory implements Result {

        public static Inventory of(InventoryCallback callback) {
            return new Inventory(callback);
        }

        @FunctionalInterface
        public interface InventoryCallback {
            String execute(List<Item> items);
        }

        private final InventoryCallback callback;

        public Inventory(InventoryCallback callback) {
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

        public static Swap of(Item item1, Item item2) {
            return new Swap(item1, item2);
        }

        private final Item item1;
        private final Item item2;

        public Swap(Item item1, Item item2) {
            this.item1 = item1;
            this.item2 = item2;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            item1.drop(item2.drop(item1.drop(Room.NOWHERE)));
        }
    }

    public static class Goto implements Result {

        public static Goto of(Room room) {
            return new Goto(room);
        }

        private final Room room;

        public Goto(Room room) {
            this.room = room;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            gameState.moveTo(room);
        }
    }

    public static final Result Drop = ((command, gameState, display) -> gameState.drop(command.getNoun()));
}
