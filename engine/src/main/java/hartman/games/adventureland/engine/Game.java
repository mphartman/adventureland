package hartman.games.adventureland.engine;

/**
 * A running game session with a player and an adventure.
 */
public class Game {
    private Adventure adventure;
    private Player player;
    private Interpreter interpreter;
    private GameState gameState;
}
