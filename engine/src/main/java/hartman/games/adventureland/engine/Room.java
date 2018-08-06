package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
        private Noun direction;
        private Room room;

        public Exit(Noun direction, Room room) {
            this.direction = direction;
            this.room = room;
        }

        public Exit(Noun direction) {
            this(direction, null);
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
            return Objects.equals(direction, exit.direction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction);
        }

        public static class Builder {
            private Noun direction;
            private Room room;

            public Builder exit(Noun direction) {
                this.direction = direction;
                return this;
            }

            public Builder towards(Room room) {
                this.room = room;
                return this;
            }

            public Builder towardsSelf() {
                this.room = null;
                return this;
            }

            public Exit build() {
                if (room == null) {
                    return new Exit(direction);
                } else {
                    return new Exit(direction, room);
                }
            }
        }
    }

    private final String name;
    private final String description;
    private final Set<Exit> exits = new LinkedHashSet<>();

    /**
     * Creates a new empty room with no exits.
     */
    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Creates a new empty room with the given exits.
     */
    public Room(String name, String description, Exit... exits) {
        this.name = name;
        this.description = description;
        // exits to NULL are replaced with exits back to this room.
        this.exits.addAll(Arrays.stream(exits)
                .map(x -> x.getRoom() == null ? new Exit(x.getDirection(), this) : x)
                .collect(Collectors.toSet()));
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
        return Objects.equals(name, room.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
