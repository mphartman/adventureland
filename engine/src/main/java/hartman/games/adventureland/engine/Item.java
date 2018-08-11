package hartman.games.adventureland.engine;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Things in a room, some of which can be picked up, carried around and dropped.
 * <p>
 * Items are either "objects" like keys, swords, lamps, and mud while other items
 * are "scenery" like trees, signs, crypts, tables, altars, donkeys, etc.
 */
public class Item extends Noun implements GameElement {

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

    private static final String ITEM_IS_DESTROYED = "Item is destroyed.";

    private static final Room INVENTORY = new Room("Inventory", "Player's inventory of carried items.");

    private final String description;
    private final boolean portable;
    private final Room startingRoom;

    private Room currentRoom;
    private boolean destroyed;

    protected Item(String name, String description, boolean portable, Room startingRoom, String... aliases) {
        super(name, aliases);
        this.description = description;
        this.portable = portable;
        this.startingRoom = startingRoom;
        this.currentRoom = startingRoom;
    }

    public String getDescription() {
        return description == null ? getName() : description;
    }

    public boolean isPortable() {
        return portable && !destroyed;
    }

    public boolean isCarried() {
        return isHere(INVENTORY);
    }

    public boolean isHere(Room room) {
        if (destroyed) return false;
        return currentRoom.equals(room);
    }

    public Boolean hasMoved() {
        if (destroyed) return false;
        return !currentRoom.equals(startingRoom);
    }

    public Room drop(Room room) {
        if (destroyed) throw new IllegalStateException(ITEM_IS_DESTROYED);
        Room formerLocation = currentRoom;
        currentRoom = room;
        return formerLocation;
    }

    public Room stow() {
        if (destroyed) throw new IllegalStateException(ITEM_IS_DESTROYED);
        if (portable) {
            return drop(INVENTORY);
        }
        throw new IllegalStateException("Item cannot be put into inventory. Cannot stow a non-portable item.");
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

}
