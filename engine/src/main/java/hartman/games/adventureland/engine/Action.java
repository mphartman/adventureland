package hartman.games.adventureland.engine;

import java.util.LinkedHashSet;
import java.util.Set;

import static hartman.games.adventureland.engine.core.Conditions.nounMatches;
import static hartman.games.adventureland.engine.core.Conditions.or;
import static hartman.games.adventureland.engine.core.Conditions.verbMatches;

/**
 * Things the player can do or which happen to her and that result in changes to the game world.
 */
public class Action {

    public static class Builder {

        private Set<Result> results = new LinkedHashSet<>();
        private Set<Condition> conditions = new LinkedHashSet<>();

        public Builder on(Verb verb) {
            return when(verbMatches(verb));
        }

        public Builder withNoVerb() {
            return on(Verb.NONE);
        }

        public Builder onUnrecognizedVerb() {
            return on(Verb.UNRECOGNIZED);
        }

        public Builder onAnyVerb() {
            return on(Verb.ANY);
        }

        public Builder with(Noun noun) {
            return when(nounMatches(noun));
        }

        public Builder withNoNoun() {
            return with(Noun.NONE);
        }

        public Builder the(Noun noun) {
            return with(noun);
        }

        public Builder withUnrecognizedNoun() {
            return with(Noun.UNRECOGNIZED);
        }

        public Builder withAnyNoun() {
            return with(Noun.ANY);
        }

        public Builder anything() {
            return withAnyNoun();
        }

        public Builder withAnyOf(Noun... nouns) {
            if (nouns.length > 0) {
                if (nouns.length == 1) {
                    return with(nouns[0]);
                }
                Condition c1 = null;
                for (Noun n : nouns) {
                    Condition c2 = nounMatches(n);
                    if (c1 == null) {
                        c1 = c2;
                    }
                    else {
                        c1 = or(c2, c1);
                    }
                }
                return when(c1);
            }
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
            this.results.clear();
            this.results.add(result);
            return this;
        }

        public Builder andThen(Result result) {
            this.results.add(result);
            return this;
        }

        public Action build() {
            return new Action(conditions, results);
        }
    }

    @FunctionalInterface
    public interface Condition {
        boolean matches(Command command, GameState gameState);
    }

    @FunctionalInterface
    public interface Result {
        void execute(Command command, GameState gameState, Display display);
    }

    private final Set<Condition> conditions = new LinkedHashSet<>();
    private final Set<Result> results = new LinkedHashSet<>();

    protected Action(Set<Condition> conditions, Set<Result> results) {
        this.conditions.addAll(conditions);
        this.results.addAll(results);
    }

    /**
     * Runs this Action if all conditions are met.
     *
     * @return true if this action is applicable to the given command and all conditions are met, otherwise returns false.
     */
    public boolean run(GameState gameState, Display display, Command command) {
        if (conditions.stream().allMatch(condition -> condition.matches(command, gameState))) {
            results.forEach(result -> result.execute(command, gameState, display));
            return true;
        }
        return false;
    }

    /**
     * Runs this Action if all conditions are met.
     *
     * @return true if this action's all conditions are met, otherwise returns false.
     */
    public boolean run(GameState gameState, Display display) {
        return run(gameState, display, Command.NONE);
    }

}
