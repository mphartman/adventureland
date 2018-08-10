package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Verb;

public final class Verbs {

    public static final Verb HELP = new Verb("HELP", "?");

    public static final Verb QUIT = new Verb("QUIT", "Q");

    public static final Verb INVENTORY = new Verb("INVENTORY", "I");

    public static final Verb LOOK = new Verb("LOOK", "L");

    public static final Verb GO = new Verb("GO", "G", "GOTO", "ENTER", "WALK", "RUN", "EXIT", "LEAVE" );

    public static final Verb OPEN = new Verb("OPEN", "UNLOCK");

    public static final Verb GET = new Verb("GET", "PICKUP", "GRAB", "TAKE");

    public static final Verb DROP = new Verb("DROP", "DISCARD");

    public static final Verb USE = new Verb("USE");
}
