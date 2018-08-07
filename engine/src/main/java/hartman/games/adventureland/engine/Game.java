package hartman.games.adventureland.engine;

/**
 * A running game session with a player and an adventure.
 */
public class Game {
    private final Adventure adventure;
    private final Interpreter interpreter;
    private final GameState gameState;
    private final Display display;

    public Game(Adventure adventure, Interpreter interpreter, GameState gameState, Display display) {
        this.adventure = adventure;
        this.interpreter = interpreter;
        this.gameState = gameState;
        this.display = display;
    }

    public void run() {
        while (gameState.isRunning()) {
            runOccurs();
            PlayerCommand playerCommand = interpreter.nextCommand();
            runActions(playerCommand);
        }
    }

    private void runOccurs() {
        adventure.getOccurs().forEach(occur -> occur.run(new ActionContext(gameState, display)));
    }

    private void runActions(PlayerCommand playerCommand) {
        adventure.getActions().forEach(action -> action.run(new ActionContext(gameState, display, playerCommand)));
    }
}
