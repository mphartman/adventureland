package hartman.games.adventureland.engine;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Rooms make up a connected network of nodes between which the player may move.
 * A Room may have no more than one exit per direction but each exit may point to the same room.
 * E.g. A room can only have one North exit but the North and Up exits can point reference the same destination.
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Room NOWHERE = new Room("nowhere", "I am no where.  It's dark and I am alone.");

    public static class Exit implements Serializable {
        private static final long serialVersionUID = 1L;

        private final Word direction;
        private final Room room;

        public Exit(Word direction, Room room) {
            Objects.requireNonNull(direction, "Exit must have a direction.");
            Objects.requireNonNull(room, "Exit must have a target room.");
            this.direction = direction;
            this.room = room;
        }

        public Word getDirection() {
            return direction;
        }

        public Room getRoom() {
            return room;
        }

        public String getDescription() {
            return direction.getName();
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setExitTowardsSelf(Word direction) {
        setExit(direction, this);
    }

    public void setExit(Word direction, Room towards) {
        setExit(new Exit(direction, towards));
    }

    public void setExit(Exit exit) {
        if (!exits.add(exit)) {
            exits.remove(exit);
            exits.add(exit);
        }
    }

    public boolean hasExit(Word direction) {
        return exits.stream().anyMatch(e -> e.getDirection().matches(direction));
    }

    public Room exit(Word direction) {
        return exits.stream()
                .filter(e -> e.getDirection().matches(direction))
                .findFirst()
                .map(Exit::getRoom)
                .orElseThrow(() -> new IllegalStateException(String.format("Invalid exit. There is no exit %s from this room.", direction)));
    }

    public Set<Exit> getExits() {
        return Collections.unmodifiableSet(exits);
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
