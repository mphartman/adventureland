package hartman.games.adventureland.engine;

/**
 * The player's desired action represented by a two-word phrase made up of a verb and noun.
 */
public class PlayerCommand {
    private final Vocabulary.Verb verb;
    private final Vocabulary.Noun noun;
    private final GameState gameState;

    public PlayerCommand(Vocabulary.Verb verb, Vocabulary.Noun noun, GameState gameState) {
        this.verb = verb;
        this.noun = noun;
        this.gameState = gameState;
    }

    public Vocabulary.Verb getVerb() {
        return verb;
    }

    public Vocabulary.Noun getNoun() {
        return noun;
    }

    public GameState getGameState() {
        return gameState;
    }
}
