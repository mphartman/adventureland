package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Things the player can do or which happen to her and that result in changes to the game world.
 */
public class Action {

    public static class Builder {

        public static Builder newBuilder() {
            return new Builder();
        }

        private Verb verb = Verb.NONE;
        private Noun noun = Noun.NONE;
        private Set<Result> results = new LinkedHashSet<>();
        private Set<Condition> conditions = new LinkedHashSet<>();

        public Builder verb(Verb verb) {
            this.verb = verb;
            return this;
        }

        public Builder noun(Noun noun) {
            this.noun = noun;
            return this;
        }

        public Builder when(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder and(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder then(Result result) {
            this.results.add(result);
            return this;
        }

        public Builder andThen(Result result) {
            this.results.add(result);
            return this;
        }

        public Action build() {
            return new Action(verb, noun, conditions, results);
        }
    }

    public static Set<Condition> setOf(Condition... conditions) {
        return new LinkedHashSet<>(Arrays.asList(conditions));
    }

    public static Set<Result> setOf(Result... results) {
        return new LinkedHashSet<>(Arrays.asList(results));
    }

    @FunctionalInterface
    public interface Condition {
        boolean matches(Command command, GameState gameState);
    }

    @FunctionalInterface
    public interface Result {
        void execute(Command command, GameState gameState, Display display);
    }

    private final Verb verb;
    private final Noun noun;
    private final Set<Condition> conditions = new LinkedHashSet<>();
    private final Set<Result> results = new LinkedHashSet<>();

    public Action(Set<Result> results) {
        this.verb = Verb.NONE;
        this.noun = Noun.NONE;
        this.results.addAll(results);
    }

    public Action(Result... results) {
        this(new LinkedHashSet<>(Arrays.asList(results)));
    }

    public Action(Set<Condition> conditions, Set<Result> results) {
        this.verb = Verb.NONE;
        this.noun = Noun.NONE;
        this.conditions.addAll(conditions);
        this.results.addAll(results);
    }

    public Action(Verb verb, Set<Result> results) {
        this.verb = verb;
        this.noun = Noun.NONE;
        this.results.addAll(results);
    }

    public Action(Verb verb, Result... results) {
        this(verb, new LinkedHashSet<>(Arrays.asList(results)));
    }

    public Action(Verb verb, Noun noun, Set<Result> results) {
        this.verb = verb;
        this.noun = noun;
        this.results.addAll(results);
    }

    public Action(Verb verb, Noun noun, Result... results) {
        this(verb, noun, new LinkedHashSet<>(Arrays.asList(results)));
    }

    public Action(Verb verb, Noun noun, Set<Condition> conditions, Set<Result> results) {
        this.verb = verb;
        this.noun = noun;
        this.conditions.addAll(conditions);
        this.results.addAll(results);
    }

    /**
     * Runs this Action if the command matches and all conditions are met.
     *
     * @param context
     * @return true if this action is applicable to the given command and all conditions are met, otherwise returns false.
     */
    public boolean run(ActionContext context) {

        Command command = context.getCommand();
        Display display = context.getDisplay();
        GameState gameState = context.getGameState();

        if (verb.equals(command.getVerb()) && noun.equals(command.getNoun())) {
            if (conditions.stream().allMatch(condition -> condition.matches(command, gameState))) {
                results.forEach(result -> result.execute(command, gameState, display));
                return true;
            }
        }
        return false;
    }

}
