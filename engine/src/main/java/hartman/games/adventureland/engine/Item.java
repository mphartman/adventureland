package hartman.games.adventureland.engine;

/**
 * Things in a room, some of which can be picked up, carried around and dropped.
 *
 * Items are either "objects" like keys, swords, lamps, and mud while other items
 * are "scenery" like trees, signs, crypts, tables, altars, donkeys, etc.
 *
 */
public class Item {
    private int id;
    private String name;
    private String description;
    private boolean carryable;

}
