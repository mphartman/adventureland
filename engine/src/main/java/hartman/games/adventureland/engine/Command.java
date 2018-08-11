package hartman.games.adventureland.engine;

import java.util.StringJoiner;

/**
 * The player's desired action represented by a two-word phrase made up of a verb and noun.
 */
public class Command {
    public static final Command NONE = new Command(Verb.NONE, Noun.NONE);

    private final Verb verb;
    private final Noun noun;

    public Command(Verb verb, Noun noun) {
        this.verb = verb;
        this.noun = noun;
    }

    public Verb getVerb() {
        return verb;
    }

    public Noun getNoun() {
        return noun;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Command.class.getSimpleName() + "[", "]")
                .add("verb=" + verb)
                .add("noun=" + noun)
                .toString();
    }
}
