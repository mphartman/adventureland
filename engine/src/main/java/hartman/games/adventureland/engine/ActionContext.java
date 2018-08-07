package hartman.games.adventureland.engine;

public class ActionContext {
    private final PlayerCommand playerCommand;
    private final Display display;
    private final GameState gameState;

    public ActionContext(GameState gameState, Display display) {
        this.gameState = gameState;
        this.display = display;
        this.playerCommand = PlayerCommand.NONE;
    }

    public ActionContext(GameState gameState, Display display, PlayerCommand playerCommand) {
        this.gameState = gameState;
        this.display = display;
        this.playerCommand = playerCommand;
    }

    public PlayerCommand getPlayerCommand() {
        return playerCommand;
    }

    public Display getDisplay() {
        return display;
    }

    public GameState getGameState() {
        return gameState;
    }
}
