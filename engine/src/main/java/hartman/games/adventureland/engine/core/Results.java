package hartman.games.adventureland.engine.core;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import hartman.games.adventureland.engine.Action.Result;
import hartman.games.adventureland.engine.Direction;
import hartman.games.adventureland.engine.Room;

public final class Results {
    private Results() {
        throw new IllegalStateException();
    }

    public static Set<Result> asSet(Result... results) {
        return new LinkedHashSet<>(Arrays.asList(results));
    }

    public static final Result GOTO_ROOM = (playerCommand, gameState) -> {
        Direction desiredExit = Direction.valueOf(playerCommand.getNoun().getName());
        Room nextRoom = gameState.getPlayerCurrentPosition().exit(desiredExit);
        gameState.movePlayerTo(nextRoom);
    };
}
