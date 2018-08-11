package hartman.games.adventureland.engine;

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
        while (gameState.isRunning()) {
            runOccurs();
            if (gameState.isRunning()) {
                runActions(interpreter.nextCommand());
            }
        }
    }

    private void runOccurs() {
        adventure.getOccurs().forEach(occur -> occur.run(gameState, display));
    }

    private void runActions(Command command) {
        for (Action action : adventure.getActions())
            if (action.run(gameState, display, command)) return;
    }
}
