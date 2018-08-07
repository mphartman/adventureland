package hartman.games.adventureland.engine;

/**
 * The player's desired action represented by a two-word phrase made up of a verb and noun.
 */
public class PlayerCommand {
    public static final PlayerCommand NONE = new PlayerCommand(Verb.NONE, Noun.NONE);

    private final Verb verb;
    private final Noun noun;

    public PlayerCommand(Verb verb, Noun noun) {
        this.verb = verb;
        this.noun = noun;
    }

    public PlayerCommand(Verb verb) {
        this.verb = verb;
        this.noun = Noun.NONE;
    }

    public Verb getVerb() {
        return verb;
    }

    public Noun getNoun() {
        return noun;
    }

    @Override
    public String toString() {
        return "PlayerCommand{" +
                verb.getName() +
                ", " + noun.getName() +
                '}';
    }
}
