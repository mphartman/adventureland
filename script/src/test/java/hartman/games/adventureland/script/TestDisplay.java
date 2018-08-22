package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class TestDisplay implements Display {

    private static final String NEWLINE = System.getProperty("line.separator");

    private StringBuilder out = new StringBuilder();

    @Override
    public void print(String message) {
        out.append(message);
    }

    @Override
    public void look(Room room, List<Item> itemsInRoom) {
        StringBuilder builder = new StringBuilder();
        builder.append(room.getDescription()).append(NEWLINE);
        if (!room.getExits().isEmpty()) {
            builder.append("Room exits: ")
                    .append(room.getExits().stream().map(Room.Exit::getDescription).collect(joining(", ")))
                    .append(NEWLINE);
        }
        if (!itemsInRoom.isEmpty()) {
            builder.append("Room items: ")
                    .append(itemsInRoom.stream().map(Item::getDescription).collect(joining(", ")))
                    .append(NEWLINE);
        }
        print(builder.toString());
    }

    @Override
    public void inventory(List<Item> itemsCarried) {
        if (itemsCarried.isEmpty()) {
            print("Inventory is empty." + NEWLINE);
        } else {
            print("Inventory items: ");
            print(itemsCarried.stream().map(Item::getDescription).collect(joining(", ")) + NEWLINE);
        }
    }

    public void reset() {
        out.setLength(0);
    }

    @Override
    public String toString() {
        return out.toString();
    }
}
