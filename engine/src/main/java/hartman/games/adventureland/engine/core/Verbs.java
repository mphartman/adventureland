package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Verb;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Verbs {

    private Verbs() {
        throw new IllegalStateException();
    }

    public static Set<Verb> asSet(Verb... verbs) {
        return new LinkedHashSet<>(Arrays.asList(verbs));
    }

    public static final Verb QUIT = new Verb("QUIT", "Q");

    public static final Verb LOOK = new Verb("LOOK", "L");

    public static final Verb GO = new Verb("GO", "G", "ENTER", "WALK", "RUN" );

}
