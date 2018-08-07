package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Things the player can do or which happen to her and that result in changes to the game world.
 */
public class Action {
    private final Verb verb;
    private final Noun noun;
    private final Set<Condition> conditions = new LinkedHashSet<>();
    private final Set<Result> results = new LinkedHashSet<>();

    @FunctionalInterface
    public interface Condition {
        boolean matches(Command command, GameState gameState);
    }

    @FunctionalInterface
    public interface Result {
        void execute(Command command, GameState gameState, Display display);
    }

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

    public void run(ActionContext context) {

        Command command = context.getCommand();
        Display display = context.getDisplay();
        GameState gameState = context.getGameState();

        if (verb.equals(command.getVerb()) && noun.equals(command.getNoun())) {
            if (conditions.stream().allMatch(condition -> condition.matches(command, gameState))) {
                results.forEach(result -> result.execute(command, gameState, display));
            }
        }

    }

}
