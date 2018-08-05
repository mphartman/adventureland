package hartman.games.adventureland.engine;

/**
 * A running game session with a player and an adventure.
 */
public class Game {
    private final Adventure adventure;
    private final Interpreter interpreter;
    private final GameState gameState;

    public Game(Adventure adventure, Interpreter interpreter, GameState gameState) {
        this.adventure = adventure;
        this.interpreter = interpreter;
        this.gameState = gameState;
    }

    public void tick() {
        PlayerCommand playerCommand = interpreter.nextCommand();
        adventure.getActions().forEach(action -> action.run(playerCommand, gameState));
    }
}
