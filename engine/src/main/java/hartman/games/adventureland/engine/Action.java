package hartman.games.adventureland.engine;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Things the player can do or which happen to her and that result in changes to the game world.
 */
public class Action {

    public static class Builder {

        private Set<Result> results = new LinkedHashSet<>();
        private Set<Condition> conditions = new LinkedHashSet<>();

        public Builder when(Condition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder and(Condition condition) {
            return when(condition);
        }

        public Builder then(Result result) {
            this.results.add(result);
            return this;
        }

        public Builder andThen(Result result) {
            return then(result);
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

    public Action(Set<Condition> conditions, Set<Result> results) {
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

}
