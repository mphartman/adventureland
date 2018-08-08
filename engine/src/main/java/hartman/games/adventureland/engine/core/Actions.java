package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;

public final class Actions {
    private Actions() {
        throw new IllegalStateException();
    }

    public static final Action QuitAction = new Action(Verbs.QUIT, Results.Quit);
}
