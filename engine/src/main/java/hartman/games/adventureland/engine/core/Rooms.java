package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;

public final class Rooms {

    public static Rooms newRoomSet() {
        return new Rooms();
    }

    public class RoomBuilder {
        private String name;
        private String description;
        private List<RoomExitHolder> roomExitHolders = new ArrayList<>();

        private RoomBuilder() {
        }

        public RoomBuilder named(String name) {
            this.name = name;
            return this;
        }

        public RoomBuilder describedAs(String description) {
            this.description = description;
            return this;
        }

        public RoomExitBuilder withExit() {
            return new RoomExitBuilder(this);
        }

        public void build() {
            RoomHolder roomHolder = new RoomHolder(new Room(name, description), roomExitHolders);
            Rooms.this.roomHolders.add(roomHolder);
        }

        void addExit(RoomExitHolder roomExitHolder) {
            roomExitHolders.add(roomExitHolder);
        }
    }

    public class RoomExitBuilder {
        private RoomBuilder roomBuilder;
        private String direction;
        private String targetRoomName;

        private RoomExitBuilder(RoomBuilder roomBuilder) {
            this.roomBuilder = roomBuilder;
        }

        public RoomExitBuilder inDirectionOf(String direction) {
            this.direction = direction;
            return this;
        }

        public RoomExitBuilder towards(String targetRoomName) {
            this.targetRoomName = targetRoomName;
            return this;
        }

        public RoomExitBuilder towardsSelf() {
            this.targetRoomName = null;
            return this;
        }

        public RoomBuilder buildExit() {
            Word directionWord = new Word(this.direction);
            Rooms.this.words.add(directionWord);
            RoomExitHolder roomExitHolder = new RoomExitHolder(directionWord, targetRoomName);
            roomBuilder.addExit(roomExitHolder);
            return roomBuilder;
        }
    }

    private class RoomHolder {
        private final Room room;
        private final List<RoomExitHolder> exits;

        RoomHolder(Room room, List<RoomExitHolder> exits) {
            this.room = room;
            this.exits = exits;
        }

        Room getRoom() {
            return room;
        }

        String getRoomName() {
            return room.getName();
        }

        List<RoomExitHolder> getExits() {
            return exits;
        }

        void setExitTowardsSelf(Word direction) {
            room.setExitTowardsSelf(direction);
        }

        void setExit(Word direction, Room towards) {
            room.setExit(direction, towards);
        }

    }

    private class RoomExitHolder {
        private final Word direction;
        private final String roomName;

        RoomExitHolder(Word direction, String roomName) {
            this.direction = direction;
            this.roomName = roomName;
        }

        Word getDirection() {
            return direction;
        }

        String getRoomName() {
            return roomName;
        }
    }

    private Set<RoomHolder> roomHolders = new LinkedHashSet<>();
    private Set<Word> words = new LinkedHashSet<>();

    private Rooms() {
    }

    public RoomBuilder newRoom() {
        return new RoomBuilder();
    }

    public Set<Room> copyOfRooms() {
        resolveExits();
        return roomHolders.stream().map(RoomHolder::getRoom).collect(toCollection(LinkedHashSet::new));
    }

    private void resolveExits() {
        // resolve the room names to each exit to point to the referenced room object
        roomHolders.forEach(roomHolder ->
                roomHolder.getExits().forEach(roomExitHolder -> {

                    String exitRoomName = roomExitHolder.getRoomName();

                    if (null == exitRoomName || exitRoomName.equals(roomHolder.getRoomName())) {
                        roomHolder.setExitTowardsSelf(roomExitHolder.getDirection());

                    } else {
                        roomHolders.stream()
                                .filter(holder -> holder.getRoomName().equals(exitRoomName))
                                .map(RoomHolder::getRoom)
                                .findFirst()
                                .<Runnable>map(room -> () -> roomHolder.setExit(roomExitHolder.getDirection(), room))
                                .orElse(() -> {
                                    throw new IllegalStateException(String.format("Invalid exit. Room %s is not defined.", exitRoomName));
                                }).run(); // could be replaced with Java 9 ifPresentOrElse
                    }
                }));
    }

    public Vocabulary buildVocabulary() {
        return new Vocabulary(words);
    }

}
