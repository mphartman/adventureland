package hartman.games.adventureland.engine;

/**
 * A running game session with a player and an adventure.
 */
public class Game {
    private final Adventure adventure;
    private final CommandInterpreter interpreter;
    private final Display display;

    private GameState gameState;

    public Game(Adventure adventure, CommandInterpreter interpreter, Display display, GameState gameState) {
        this.adventure = adventure;
        this.interpreter = interpreter;
        this.display = display;
        this.gameState = gameState;
    }

    public GameState run() {
        runOccurs();
        while (gameState.isRunning()) {
            gameState = takeTurn(interpreter.nextCommand());
        }
        return gameState;
    }

    public GameState takeTurn(Command command) {
        runActions(command);
        runOccurs();
        return gameState;
    }

    private void runOccurs() {
        adventure.getOccurs().forEach(occur -> occur.run(gameState, display, Command.NONE));
    }

    private void runActions(Command command) {
        for (Action action : adventure.getActions())
            if (action.run(gameState, display, command)) return;
    }
}
