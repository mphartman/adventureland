package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class DefaultDisplay implements Display {

    private final PrintWriter out;

    public DefaultDisplay(PrintWriter writer) {
        this.out = writer;
    }

    @Override
    public void print(String message) {
        out.print(message);
        out.flush();
    }

    @Override
    public void look(Room room, List<Item> itemsInRoom) {
        StringBuilder buf = new StringBuilder();
        buf.append(format("%s%n", room.getDescription()));
        lookAtItems(itemsInRoom, buf);
        lookAtExits(room.getExits(), buf);
        print(buf.toString());
    }

    private void lookAtItems(List<Item> itemsInRoom, StringBuilder buf) {
        if (!itemsInRoom.isEmpty()) {
            buf.append(format("%n"));
            int numOfItems = itemsInRoom.size();
            if (numOfItems == 1) {
                buf.append(format("I can also see %s", itemsInRoom.get(0).getDescription()));
            } else {
                buf.append(format("I can also see %d other things here: ", numOfItems));
                IntStream.range(0, numOfItems).forEachOrdered(i -> {
                    if (i > 0) {
                        buf.append(", ");
                    }
                    if (i == (numOfItems - 1)) {
                        buf.append("and ");
                    }
                    buf.append(itemsInRoom.get(i).getDescription());
                });
            }
            buf.append(format("%n"));
        }
    }

    private void lookAtExits(Set<Room.Exit> exits, StringBuilder buf) {
        buf.append(format("%n"));
        if (exits.isEmpty()) {
            buf.append("There are no obvious exits.");
        } else {
            if (exits.size() == 1) {
                buf.append(format("There is a single exit to the %s", exits.iterator().next().getDescription()));
            } else {
                String exitsString = String.join(", ", exits.stream().map(Room.Exit::getDescription).collect(Collectors.toList()));
                buf.append(format("There are %d obvious exits: %s", exits.size(), exitsString));
            }
        }
        buf.append(format("%n"));
    }

    @Override
    public void inventory(List<Item> itemsCarried) {
        StringBuilder buf = new StringBuilder();
        if (itemsCarried.isEmpty()) {
            buf.append("I'm not carrying anything right now.");
        } else {
            buf.append("I'm carrying ");
            if (itemsCarried.size() == 1) {
                buf.append(itemsCarried.iterator().next().getDescription());
            } else {
                buf.append(format("%d things: ", itemsCarried.size()));
                itemsCarried.forEach(item -> buf.append(format("%n - %s", item.getDescription())));
            }
        }
        buf.append(format("%n"));
        print(buf.toString());
    }

}
