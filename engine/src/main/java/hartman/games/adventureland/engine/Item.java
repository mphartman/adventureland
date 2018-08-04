package hartman.games.adventureland.engine;

import java.util.Objects;

/**
 * Things in a room, some of which can be picked up, carried around and dropped.
 *
 * Items are either "objects" like keys, swords, lamps, and mud while other items
 * are "scenery" like trees, signs, crypts, tables, altars, donkeys, etc.
 *
 */
public class Item {
    private String name;
    private String description;
    private boolean carryable;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
        this.carryable = false;
    }
    
    public Item(String name, String description, Boolean carryable) {
        this.name = name;
        this.description = description;
        this.carryable = carryable;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCarryable() {
        return carryable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
}
