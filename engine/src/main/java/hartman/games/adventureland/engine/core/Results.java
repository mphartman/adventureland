package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Result;

public final class Results {
    private Results() {
        throw new IllegalStateException();
    }

    public static final Result QUIT = (playerCommand, gameState, display) -> gameState.quit();

    public static final Result GOTO = (playerCommand, gameState, display) -> gameState.exitTowards(playerCommand.getNoun());
}
