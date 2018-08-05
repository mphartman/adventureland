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

    public static final Verb OCCURS = new Verb("OCCURS");

    public static final Verb GO = new Verb("GO", "G", "ENTER", "WALK", "RUN" );

}
