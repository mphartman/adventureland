package hartman.games.adventureland.engine;

import java.util.StringJoiner;

/**
 * The player's desired action represented by a two-word phrase made up of a verb and noun.
 */
public class Command {
    public static final Command NONE = new Command(Word.NONE, Word.NONE);

    private final Word verb;
    private final Word noun;

    public Command(Word verb, Word noun) {
        this.verb = verb;
        this.noun = noun;
    }

    public Word getVerb() {
        return verb;
    }

    public Word getNoun() {
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
