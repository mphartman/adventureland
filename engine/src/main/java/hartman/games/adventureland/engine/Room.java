package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Rooms make up a connected network of nodes between which the player may move.
 *
 * A room may also contain {@link Item}s.
 */
public class Room {

    public static final Room NOWHERE = new Room("nowhere", "No where.");
    
    public static class Exit {
        private Noun direction;
        private Room room;

        public Exit(Noun direction, Room room) {
            this.direction = direction;
            this.room = room;
        }

        public Exit(Noun direction) {
            this(direction, NOWHERE);
        }

        public Noun getDirection() {
            return direction;
        }

        public Room getRoom() {
            return room;
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

    private String name;
    private String description;
    private Set<Exit> exits = new LinkedHashSet<>();
    private Set<Item> items = new LinkedHashSet<>();

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
        this.exits.addAll(Arrays.asList(exits).stream()
                .map(x -> x.getRoom().equals(NOWHERE) ? new Exit(x.getDirection(), this) : x)
                .collect(Collectors.toSet()));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasExit(Noun direction) {
        return exits.stream().anyMatch(e -> e.getDirection().equals(direction));
    }

    public Room exit(Noun direction) {
        return exits.stream()
                .filter(e -> e.getDirection().equals(direction))
                .findFirst()
                .map(Exit::getRoom)
                .orElseThrow(() -> new IllegalStateException(String.format("Invalid exit. There is no exit %s from this room.", direction)));
    }

    public boolean containsItem(Item item) {
        return items.contains(item);
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
