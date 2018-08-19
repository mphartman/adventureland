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

        buf.append(format("%n%s%n", room.getDescription()));

        lookAtItems(itemsInRoom, buf);

        lookAtExits(room.getExits(), buf);

        print(buf.toString());
    }

    private void lookAtItems(List<Item> itemsInRoom, StringBuilder buf) {
        if (!itemsInRoom.isEmpty()) {
            int numOfItems = itemsInRoom.size();
            if (numOfItems == 1) {
                buf.append(format("I can also see %s%n", itemsInRoom.get(0).getDescription()));
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
                buf.append('\n');
            }
        }
    }

    private void lookAtExits(Set<Room.Exit> exits, StringBuilder buf) {
        if (exits.isEmpty()) {
            buf.append(format("There are no obvious exits from here.%n"));
        } else {
            if (exits.size() == 1) {
                buf.append(format("There is a single exit to the %s%n", exits.iterator().next().getDescription()));
            } else {
                String exitsString = String.join(", ", exits.stream().map(Room.Exit::getDescription).collect(Collectors.toList()));
                buf.append(format("There are %d obvious exits: %s%n", exits.size(), exitsString));
            }
        }
    }

    @Override
    public void inventory(List<Item> itemsCarried) {
        StringBuilder buf = new StringBuilder();
        if (itemsCarried.isEmpty()) {
            buf.append(format("%nI'm not carrying anything right now.%n"));
        } else {
            buf.append(format("%nI'm carrying "));
            if (itemsCarried.size() == 1) {
                buf.append(format("%s%n", itemsCarried.iterator().next().getDescription()));
            } else {
                buf.append(format("%d items: ", itemsCarried.size()));
                itemsCarried.forEach(item -> buf.append(format("%n - %s", item.getDescription())));
            }
            buf.append('\n');
        }
        print(buf.toString());
    }

}
