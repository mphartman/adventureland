package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Direction;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static hartman.games.adventureland.engine.Action.Result;

public final class Results {
    private Results() {
        throw new IllegalStateException();
    }

    public static Set<Result> asSet(Result... results) {
        return new LinkedHashSet<>(Arrays.asList(results));
    }

    public static final Result GOTO_ROOM = (pc) -> {
        Direction desiredExit = Direction.valueOf(pc.getNoun().getName());
        GameState gameState = pc.getGameState();
        Room nextRoom = gameState.getPlayerCurrentPosition().exit(desiredExit);
        gameState.movePlayerTo(nextRoom);
    };
}
