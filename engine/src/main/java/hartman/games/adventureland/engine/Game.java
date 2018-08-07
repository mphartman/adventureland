package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Actions;

/**
 * A running game session with a player and an adventure.
 */
public class Game {
    private final Adventure adventure;
    private final CommandInterpreter interpreter;
    private final GameState gameState;
    private final Display display;

    public Game(Adventure adventure, CommandInterpreter interpreter, GameState gameState, Display display) {
        this.adventure = adventure;
        this.interpreter = interpreter;
        this.gameState = gameState;
        this.display = display;
    }

    public void run() {
        /* HACK */ Actions.LOOK_OCCURS.run(new ActionContext(gameState, display));
        while (gameState.isRunning()) {
            runOccurs();
            Command command = interpreter.nextCommand();
            runActions(command);
        }
    }

    private void runOccurs() {
        adventure.getOccurs().forEach(occur -> occur.run(new ActionContext(gameState, display)));
    }

    private void runActions(Command command) {
        adventure.getActions().forEach(action -> action.run(new ActionContext(gameState, display, command)));
    }
}
