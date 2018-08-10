package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Rooms make up a connected network of nodes between which the player may move.
 *
 * A Room may have no more than one exit per direction but each exit may point to the same room.
 *
 * E.g. A room can only have one North exit but the North and Up exits can point reference the same destination.
 */
public class Room implements GameElement {

    public static final Room NOWHERE = new Room("nowhere", "I am no where.  It's dark and I am alone.");

    public static class Exit implements GameElement {
        private final Noun direction;
        private final Room room;

        public Exit(Noun direction, Room room) {
            Objects.requireNonNull(direction, "Exit must have a direction.");
            Objects.requireNonNull(room, "Exit must have a target room.");
            this.direction = direction;
            this.room = room;
        }

        public Noun getDirection() {
            return direction;
        }

        public Room getRoom() {
            return room;
        }

        public String getDescription() {
            return direction.getName();
        }

        @Override
        public void accept(GameElementVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Exit exit = (Exit) o;

            return direction.equals(exit.direction);
        }

        @Override
        public int hashCode() {
            return direction.hashCode();
        }
    }

    private final String name;
    private final String description;
    private final Set<Exit> exits = new LinkedHashSet<>();

    /**
     * Creates a new empty room with the given exits.
     */
    public Room(String name, String description, Exit... exits) {
        Objects.requireNonNull(name, "Room must have a name.");
        Objects.requireNonNull(description, "Room must have a description");
        this.name = name;
        this.description = description;
        this.exits.addAll(Arrays.asList(exits));
    }

    /**
     * Creates a new empty room with no exits.
     */
    public Room(String name, String description) {
        this(name, description, new Exit[0]);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setExitTowardsSelf(Noun direction) {
        setExit(direction, this);
    }

    public void setExit(Noun direction, Room towards) {
        setExit(new Exit(direction, towards));
    }

    public void setExit(Exit exit) {
        if (!exits.add(exit)) {
            exits.remove(exit);
            exits.add(exit);
        }
    }

    public boolean hasExit(Noun direction) {
        return exits.stream().anyMatch(e -> e.getDirection().equals(direction));
    }

    public int numberOfExits() {
        return exits.size();
    }

    public Room exit(Noun direction) {
        return exits.stream()
                .filter(e -> e.getDirection().equals(direction))
                .findFirst()
                .map(Exit::getRoom)
                .orElseThrow(() -> new IllegalStateException(String.format("Invalid exit. There is no exit %s from this room.", direction)));
    }

    @Override
    public void accept(GameElementVisitor visitor) {
        visitor.visit(this);
        exits.forEach(exit -> visitor.visit(exit));
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        return name.equals(room.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
