package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Things the player can do or which happen to her and that result in changes to the game world.
 */
public class Action {
    private final Verb verb;
    private final Noun noun;
    private final Set<Condition> conditions = new LinkedHashSet<>();
    private final Set<Result> results = new LinkedHashSet<>();

    public interface Condition extends BiFunction<PlayerCommand, GameState, Boolean> {
    }

    public interface Result extends BiConsumer<PlayerCommand, GameState> {
    }

    public Action(Verb verb, Result... results) {
        this.verb = verb;
        this.noun = Noun.ANY;
        this.results.addAll(Arrays.asList(results));
    }

    public Action(Verb verb, Noun noun, Set<Result> results) {
        this.verb = verb;
        this.noun = noun;
        this.results.addAll(results);
    }

    public Action(Verb verb, Noun noun, Set<Condition> conditions, Set<Result> results) {
        this.verb = verb;
        this.noun = noun;
        this.conditions.addAll(conditions);
        this.results.addAll(results);
    }

    public void run(PlayerCommand playerCommand, GameState gameState) {
        if (playerCommand.getVerb().equals(verb)) {
            if (noun.equals(Noun.ANY) || playerCommand.getNoun().equals(noun)) {
                if (conditions.stream().allMatch(condition -> condition.apply(playerCommand, gameState))) {
                    results.forEach(result -> result.accept(playerCommand, gameState));
                }
            }
        }
    }

}
