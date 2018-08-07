package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Result;
import hartman.games.adventureland.engine.GameElementVisitor;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;

public final class Results {
    private Results() {
        throw new IllegalStateException();
    }

    public static final Result QUIT = (playerCommand, gameState, display) -> gameState.quit();

    public static final Result GOTO = (playerCommand, gameState, display) -> gameState.exitTowards(playerCommand.getNoun());

    public static final Result LOOK = (playerCommand, gameState, display) -> {
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
                        printf("There are %d visible exits:%n", numberOfExits);
                    }
                } else {
                    printf("There are no visible exits.%n");
                }
            }

            @Override
            public void visit(Room.Exit exit) {
                printf("%s%n", exit.getDescription());
            }
        });
        display.print(buf.toString());
    };

}
