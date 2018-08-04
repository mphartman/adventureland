package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Noun;

public final class Nouns {

    private Nouns() {
        throw new IllegalStateException();
    }

    public static final Noun ANY = new Noun("ANY");
    public static final Noun NORTH = new Noun("NORTH");
    public static final Noun SOUTH = new Noun("SOUTH");
    public static final Noun UP = new Noun("UP");
    public static final Noun DOWN = new Noun("DOWN");
    public static final Noun EAST = new Noun("EAST");
    public static final Noun WEST = new Noun("WEST");
}
