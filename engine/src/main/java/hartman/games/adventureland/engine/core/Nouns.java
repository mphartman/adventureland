package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Noun;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Nouns {

    private Nouns() {
        throw new IllegalStateException();
    }

    public static Set<Noun> directions() {
        return new LinkedHashSet<>(Arrays.asList(NORTH, SOUTH, UP, DOWN, EAST, WEST));
    }

    public static final Noun NORTH = new Noun("North", "N");
    public static final Noun SOUTH = new Noun("South", "S");
    public static final Noun UP = new Noun("Up", "U");
    public static final Noun DOWN = new Noun("Down", "D");
    public static final Noun EAST = new Noun("East", "E");
    public static final Noun WEST = new Noun("West", "W");

    public static final Noun DOOR = new Noun("Door");
}
