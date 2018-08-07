package hartman.games.adventureland.engine;

public class ActionContext {
    private final Command command;
    private final Display display;
    private final GameState gameState;

    public ActionContext(GameState gameState, Display display) {
        this.gameState = gameState;
        this.display = display;
        this.command = Command.NONE;
    }

    public ActionContext(GameState gameState, Display display, Command command) {
        this.gameState = gameState;
        this.display = display;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public Display getDisplay() {
        return display;
    }

    public GameState getGameState() {
        return gameState;
    }
}
