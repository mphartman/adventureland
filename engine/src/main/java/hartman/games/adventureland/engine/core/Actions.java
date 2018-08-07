package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Noun;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static hartman.games.adventureland.engine.Action.Condition;
import static hartman.games.adventureland.engine.Action.Result;

public final class Actions {
    private Actions() {
        throw new IllegalStateException();
    }

    private static Set<Condition> setOf(Condition... conditions) {
        return new LinkedHashSet<>(Arrays.asList(conditions));
    }

    private static Set<Result> setOf(Result... results) {
        return new LinkedHashSet<>(Arrays.asList(results));
    }

    public static final Action QUIT_ACTION = new Action(Verbs.QUIT, Results.QUIT);

    public static final Action GO_ACTION = new Action(Verbs.GO, Noun.ANY, setOf(Conditions.HAS_EXIT), setOf(Results.GOTO));
}
