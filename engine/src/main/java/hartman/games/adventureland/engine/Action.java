package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Things the player can do or which happen to her and that result in changes to the game world.
 */
public class Action {
    private final Vocabulary.Verb verb;
    private final Vocabulary.Noun noun;
    private final Set<Condition> conditions = new LinkedHashSet<>();
    private final Set<Result> results = new LinkedHashSet<>();

    public interface Condition extends Function<PlayerCommand, Boolean> {
    }

    public interface Result extends Consumer<PlayerCommand> {
    }

    public Action(Vocabulary.Verb verb, Result... results) {
        this.verb = verb;
        this.noun = Vocabulary.Noun.UNRECOGNIZED;
        this.results.addAll(Arrays.asList(results));
    }

    public Action(Vocabulary.Verb verb, Vocabulary.Noun noun, Set<Result> results) {
        this.verb = verb;
        this.noun = noun;
        this.results.addAll(results);
    }

    public Action(Vocabulary.Verb verb, Vocabulary.Noun noun, Set<Condition> conditions, Set<Result> results) {
        this.verb = verb;
        this.noun = noun;
        this.conditions.addAll(conditions);
        this.results.addAll(results);
    }

    public void run(PlayerCommand playerCommand) {
        if (playerCommand.getVerb().equals(verb)) {
            if (conditions.stream().allMatch(condition -> condition.apply(playerCommand))) {
                results.forEach(result -> result.accept(playerCommand));
            }
        }
    }

}
