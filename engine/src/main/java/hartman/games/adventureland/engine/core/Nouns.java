package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Noun;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Nouns {

    private Nouns() {
        throw new IllegalStateException();
    }

    public static Set<Noun> asSet(Noun... nouns) {
        return new LinkedHashSet<>(Arrays.asList(nouns));
    }

    public static Set<Noun> directions() {
        return asSet(NORTH, SOUTH, UP, DOWN, EAST, WEST);
    }

    public static final Noun NORTH = new Noun("North");
    public static final Noun SOUTH = new Noun("South");
    public static final Noun UP = new Noun("Up");
    public static final Noun DOWN = new Noun("Down");
    public static final Noun EAST = new Noun("East");
    public static final Noun WEST = new Noun("West");
}
