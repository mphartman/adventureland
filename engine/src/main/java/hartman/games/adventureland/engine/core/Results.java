package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.*;
import hartman.games.adventureland.engine.Action.Result;
import hartman.games.adventureland.engine.Command;

public final class Results {
    private Results() {
        throw new IllegalStateException();
    }

    public static final Result QUIT = (command, gameState, display) -> gameState.quit();

    public static final Result GOTO = (command, gameState, display) -> gameState.exitTowards(command.getNoun());

    public static final Result LOOK = (command, gameState, display) -> {
        StringBuffer buf = new StringBuffer();
        gameState.describe(new GameElementVisitor() {
            private void printf(String message, Object... args) {
                buf.append(String.format(message, args));
            }

            @Override
            public void visit(Item item) {
                printf(item.getDescription());
            }

            @Override
            public void visit(Room room) {
                printf("%n%s%n", room.getDescription());
                int numberOfExits = room.numberOfExits();
                if (numberOfExits > 0) {
                    if (numberOfExits == 1) {
                        printf("There is a single visible exit ");
                    } else {
                        printf("There are %d visible exits: ", numberOfExits);
                    }
                } else {
                    printf("There are no visible exits.%n");
                }
            }

            @Override
            public void visit(Room.Exit exit) {
                printf("%s, ", exit.getDescription());
            }
        });
        buf.append(System.getProperty("line.separator"));
        display.print(buf.toString());
    };

    public static class PRINT implements Result {
        private final String message;

        public PRINT(String message) {
            this.message = message;
        }

        @Override
        public void execute(Command command, GameState gameState, Display display) {
            display.print(message);
        }
    }
}
