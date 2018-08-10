package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Verb;

public final class Verbs {

    public static final Verb HELP = new Verb("HELP", "?");

    public static final Verb QUIT = new Verb("QUIT");

    public static final Verb INVENTORY = new Verb("INVENTORY", "I");

    public static final Verb LOOK = new Verb("LOOK", "L");

    public static final Verb GO = new Verb("GO", "GOTO", "ENTER", "WALK", "RUN", "EXIT", "LEAVE" );

    public static final Verb OPEN = new Verb("OPEN", "UNLOCK");

    public static final Verb GET = new Verb("GET", "PICKUP", "GRAB", "TAKE");

    public static final Verb DROP = new Verb("DROP", "DISCARD");

    public static final Verb USE = new Verb("USE");

    public static final Verb GO_NORTH = new Verb("North", "N");

    public static final Verb GO_SOUTH = new Verb("South", "S");

    public static final Verb GO_UP = new Verb("Up", "U");

    public static final Verb GO_DOWN = new Verb("Down", "D");

    public static final Verb GO_EAST = new Verb("East", "E");

    public static final Verb GO_WEST = new Verb("West", "W");
}
