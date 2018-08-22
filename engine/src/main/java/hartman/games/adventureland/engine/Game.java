package hartman.games.adventureland.engine;

/**
 * A running game session with a player and an adventure.
 */
public class Game {
    private final Adventure adventure;
    private final CommandInterpreter interpreter;
    private final Display display;

    public Game(Adventure adventure, CommandInterpreter interpreter, Display display) {
        this.adventure = adventure;
        this.interpreter = interpreter;
        this.display = display;
    }

    public GameState run(GameState gameState) {
        while (gameState.isRunning()) {
            gameState = takeTurn(gameState, interpreter);
        }
        return gameState;
    }

    public GameState takeTurn(GameState gameState, CommandInterpreter interpreter) {
        runOccurs(gameState);
        if (gameState.isRunning()) {
            runActions(gameState, interpreter.nextCommand());
        }
        return gameState;
    }

    private void runOccurs(GameState gameState) {
        adventure.getOccurs().forEach(occur -> occur.run(gameState, display, Command.NONE));
    }

    private void runActions(GameState gameState, Command command) {
        for (Action action : adventure.getActions())
            if (action.run(gameState, display, command)) return;
    }
}
