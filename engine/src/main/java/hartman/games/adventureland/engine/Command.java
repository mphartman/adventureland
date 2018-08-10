package hartman.games.adventureland.engine;

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

    public Command(Verb verb) {
        this.verb = verb;
        this.noun = Noun.NONE;
    }

    public Command(Noun noun) {
        this.verb = Verb.NONE;
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
        return "Command{" +
                verb.getName() +
                ", " + noun.getName() +
                '}';
    }
}
