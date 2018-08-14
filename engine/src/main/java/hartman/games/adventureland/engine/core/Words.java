package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Word;

public final class Words {

    private Words() {
        throw new IllegalStateException("utility class");
    }

    public static final Word HELP = new Word("HELP", "?");
    public static final Word QUIT = new Word("QUIT");
    public static final Word INVENTORY = new Word("INVENTORY", "I");
    public static final Word LOOK = new Word("LOOK", "L");
    public static final Word GO = new Word("GO", "GOTO", "ENTER", "WALK", "RUN", "EXIT", "LEAVE" );
    public static final Word OPEN = new Word("OPEN", "UNLOCK");
    public static final Word GET = new Word("GET", "PICKUP", "GRAB", "TAKE");
    public static final Word DROP = new Word("DROP", "DISCARD");
    public static final Word USE = new Word("USE");
    public static final Word NORTH = new Word("North", "N");
    public static final Word SOUTH = new Word("South", "S");
    public static final Word UP = new Word("Up", "U");
    public static final Word DOWN = new Word("Down", "D");
    public static final Word EAST = new Word("East", "E");
    public static final Word WEST = new Word("West", "W");

}
