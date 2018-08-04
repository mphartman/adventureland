package hartman.games.adventureland.engine;

/**
 * The player's desired action represented by a two-word phrase made up of a verb and noun.
 */
public class PlayerCommand {
    private final Verb verb;
    private final Noun noun;

    public PlayerCommand(Verb verb, Noun noun) {
        this.verb = verb;
        this.noun = noun;
    }

    public Verb getVerb() {
        return verb;
    }

    public Noun getNoun() {
        return noun;
    }
}
