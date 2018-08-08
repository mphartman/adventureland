package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.*;
import hartman.games.adventureland.engine.Action.Result;
import hartman.games.adventureland.engine.Command;

import java.util.*;

public final class Results {
    private Results() {
        throw new IllegalStateException();
    }

    public static final Result Quit = (command, gameState, display) -> gameState.quit();

    public static final Result Goto = (command, gameState, display) -> gameState.exitTowards(command.getNoun());

    public static class Look implements Result {

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
        private final String message;

        public Print(String message) {
            this.message = message;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            display.print(message);
        }
    }
}
