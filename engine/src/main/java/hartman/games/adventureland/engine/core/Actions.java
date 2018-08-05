package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Noun;

public final class Actions {
    private Actions() {
        throw new IllegalStateException();
    }

    public static final Action GO_ACTION = new Action(
            Verbs.GO,
            Noun.ANY,
            Conditions.asSet(Conditions.HAS_EXIT),
            Results.asSet(Results.GOTO_ROOM));
}
