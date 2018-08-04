package hartman.games.adventureland.engine.core;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import hartman.games.adventureland.engine.Action.Result;

public final class Results {
    private Results() {
        throw new IllegalStateException();
    }

    public static Set<Result> asSet(Result... results) {
        return new LinkedHashSet<>(Arrays.asList(results));
    }

    public static final Result GOTO_ROOM = (playerCommand, gameState) -> {
        gameState.exitTowards(playerCommand.getNoun());
    };
}
