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
    private boolean carried;
    private Room currentRoom = Room.NOWHERE;
    private Room inventory = Room.INVENTORY;

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

    public Item(String name, String description, Room startingLocation) {
        this.name = name;
        this.description = description;
        this.currentRoom = startingLocation;
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

    public boolean isHere(Room room) {
        return currentRoom.equals(room);
    }

    public Room drop(Room room) {
        Room formerLocation = currentRoom;
        currentRoom = room;
        carried = false;
        return formerLocation;
    }

    public Room stow() {
        if (carryable) {            
            Room formerRoom = currentRoom;
            currentRoom = inventory;
            carried = true;
            return formerRoom;
        }
        throw new IllegalStateException(String.format("Item %s cannot be put into inventory. Cannot stow a non-carryable item.", name));
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
