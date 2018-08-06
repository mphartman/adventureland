package hartman.games.adventureland.engine;

import java.util.Objects;

/**
 * Things in a room, some of which can be picked up, carried around and dropped.
 * <p>
 * Items are either "objects" like keys, swords, lamps, and mud while other items
 * are "scenery" like trees, signs, crypts, tables, altars, donkeys, etc.
 */
public class Item implements GameElement {
    private static final Room INVENTORY = new Room("Inventory", "Player's inventory of carried items.");

    private final String name;
    private final String description;
    private final boolean portable;
    private final Room startingRoom;

    private Room currentRoom;
    private Noun noun;

    protected Item(String name, String description, boolean portable, Room startingRoom) {
        this.name = name;
        this.description = description;
        this.portable = portable;
        this.startingRoom = startingRoom;
        this.currentRoom = startingRoom;
        this.noun = new Noun(name);
    }

    public static Item newSceneryRoomItem(String name, String description, Room startingRoom) {
        return new Item(name, description, false, startingRoom);
    }

    public static Item newSceneryRoomItem(String name, String description) {
        return newSceneryRoomItem(name, description, Room.NOWHERE);
    }

    public static Item newPortableObjectItem(String name, String description, Room startingRoom) {
        return new Item(name, description, true, startingRoom);
    }

    public static Item newPortableObjectItem(String name, String description) {
        return newPortableObjectItem(name, description, Room.NOWHERE);
    }

    public static Item newInventoryItem(String name, String description) {
        return newPortableObjectItem(name, description, INVENTORY);
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
        return currentRoom.equals(INVENTORY);
    }

    public boolean isHere(Room room) {
        return currentRoom.equals(room);
    }

    public Boolean hasMoved() {
        return !currentRoom.equals(startingRoom);
    }

    public Noun asNoun() {
        return noun;
    }

    public Room drop(Room room) {
        Room formerLocation = currentRoom;
        currentRoom = room;
        return formerLocation;
    }

    public Room stow() {
        if (portable) {
            Room formerRoom = currentRoom;
            currentRoom = INVENTORY;
            return formerRoom;
        }
        throw new IllegalStateException(String.format("Item %s cannot be put into inventory. Cannot stow a non-portable item.", name));
    }

    @Override
    public void accept(GameElementVisitor visitor) {
        visitor.visit(this);
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
