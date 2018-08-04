package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Direction;

import static hartman.games.adventureland.engine.Vocabulary.Noun;

public final class Nouns {

    private Nouns() {
        throw new IllegalStateException();
    }

    public static final Noun ANY = new Noun("ANY");

    public static final Noun UP = new Noun(Direction.UP.name());

    public static final Noun DOWN = new Noun(Direction.DOWN.name());



}
