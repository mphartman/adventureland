package hartman.games.adventureland.engine;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Things in a room, some of which can be picked up, carried around and dropped.
 * <p>
 * Items are either "objects" like keys, swords, lamps, and mud while other items
 * are "scenery" like trees, signs, crypts, tables, altars, donkeys, etc.
 */
public class Item implements GameElement {

    public static class Builder {
        private String name;
        private String description;
        private boolean portable;
        private Room startingRoom = Room.NOWHERE;
        private Set<String> aliases = new LinkedHashSet<>();

        public Builder named(String name) {
            this.name = name;
            return this;
        }

        public Builder describedAs(String description) {
            this.description = description;
            return this;
        }

        public Builder portable() {
            this.portable = true;
            return this;
        }

        public Builder in(Room room) {
            this.startingRoom = room;
            return this;
        }

        public Builder inInventory() {
            this.startingRoom = INVENTORY;
            this.portable = true;
            return this;
        }

        public Builder alias(String alias) {
            this.aliases.add(alias);
            return this;
        }

        public Item build() {
            return new Item(name, description, portable, startingRoom, aliases.toArray(new String[0]));
        }
    }

    private static final Room INVENTORY = new Room("Inventory", "Player's inventory of carried items.");

    private final String name;
    private final String description;
    private final boolean portable;
    private final Room startingRoom;
    private final Noun noun;

    private Room currentRoom;
    private boolean destroyed;

    protected Item(String name, String description, boolean portable, Room startingRoom, String... aliases) {
        Objects.requireNonNull(name, "Item must have a name.");
        this.name = name;
        this.description = description;
        this.portable = portable;
        this.startingRoom = startingRoom;
        this.currentRoom = startingRoom;
        this.noun = new Noun(name, aliases);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPortable() {
        return portable;
    }

    public boolean isCarried() {
        return isHere(INVENTORY);
    }

    public boolean isHere(Room room) {
        if (destroyed) throw new IllegalStateException("Item is destroyed.");
        return currentRoom.equals(room);
    }

    public Boolean hasMoved() {
        if (destroyed) throw new IllegalStateException("Item is destroyed.");
        return !currentRoom.equals(startingRoom);
    }

    public Noun asNoun() {
        return noun;
    }

    public Room drop(Room room) {
        if (destroyed) throw new IllegalStateException("Item is destroyed.");
        Room formerLocation = currentRoom;
        currentRoom = room;
        return formerLocation;
    }

    public Room stow() {
        if (destroyed) throw new IllegalStateException("Item is destroyed.");
        if (portable) {
            Room formerRoom = currentRoom;
            currentRoom = INVENTORY;
            return formerRoom;
        }
        throw new IllegalStateException(String.format("Item %s cannot be put into inventory. Cannot stow a non-portable item.", name));
    }

    public void putWith(Item item) {
        drop(item.currentRoom);
    }

    public void destroy() {
        this.destroyed = true;
        this.currentRoom = Room.NOWHERE;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void accept(GameElementVisitor visitor) {
        if (!destroyed) visitor.visit(this);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Item.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("destroyed=" + destroyed)
                .add("portable=" + portable)
                .add("currentRoom=" + currentRoom)
                .add("noun=" + noun)
                .add("startingRoom=" + startingRoom)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
