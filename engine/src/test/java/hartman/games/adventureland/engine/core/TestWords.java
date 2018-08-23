package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Word;

final class TestWords {

    private TestWords() {
        throw new IllegalStateException("utility class");
    }

    static final Word GO = new Word("GO", "GOTO", "ENTER", "WALK", "RUN", "EXIT", "LEAVE");
    static final Word GET = new Word("GET", "PICKUP", "GRAB", "TAKE");
    static final Word DROP = new Word("DROP", "DISCARD");
    static final Word NORTH = new Word("North", "N");
    static final Word SOUTH = new Word("South", "S");
    static final Word UP = new Word("Up", "U");
    static final Word DOWN = new Word("Down", "D");
    static final Word EAST = new Word("East", "E");
    static final Word WEST = new Word("West", "W");

}
