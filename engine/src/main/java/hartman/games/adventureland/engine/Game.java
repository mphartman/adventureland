package hartman.games.adventureland.engine;

import java.util.Collection;

/**
 * A running game session with a player and an adventure.
 */
public class Game {
    private final Collection<Action> actions;
    private final Collection<Action> occurs;
    private final CommandInterpreter interpreter;
    private final Display display;

    public Game(Adventure adventure, CommandInterpreter interpreter, Display display) {
        this.actions = adventure.getActions();
        this.occurs = adventure.getOccurs();
        this.interpreter = interpreter;
        this.display = display;
    }

    public GameState run(GameState gameState) {
        while (gameState.isRunning()) {
            takeTurn(gameState, interpreter.nextCommand());
        }
        return gameState;
    }

    public GameState takeTurn(GameState gameState, Command command) {
        occurs.forEach(occur -> occur.run(gameState, display, Command.NONE));
        if (gameState.isRunning()) {
            for (Action action : actions) {
                if (action.run(gameState, display, command)) {
                    return gameState;
                }
            }
        }
        return gameState;
    }

}

