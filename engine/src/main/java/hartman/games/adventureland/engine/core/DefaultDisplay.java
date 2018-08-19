package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

public class DefaultDisplay implements Display {

    public static final Display CONSOLE_DISPLAY = new DefaultDisplay(new PrintWriter(System.out));

    private final Writer out;

    public DefaultDisplay(Writer writer) {
        this.out = writer;
    }

    @Override
    public void print(String message) {
        try {
            out.write(message);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void look(Room room, Set<Item> itemsInRoom) {
        List<Item> items = new ArrayList<>(itemsInRoom);
        StringBuilder buf = new StringBuilder();
        buf.append(format("%n%s%n", room.getDescription()));
        if (!items.isEmpty()) {
            if (items.size() == 1) {
                buf.append(format("I can also see %s%n", items.get(0).getDescription()));
            } else {
                buf.append(format("I can also see %d other things here: ", items.size()));
                IntStream.range(0, items.size()).forEachOrdered(i -> {
                    if (i > 0) {
                        buf.append(", ");
                    }
                    if (i == (items.size() - 1)) {
                        buf.append("and ");
                    }
                    buf.append(items.get(i).getDescription());
                });
                buf.append('\n');
            }
        }
        List<Room.Exit> exits = new ArrayList<>(room.getExits());
        if (exits.size() == 0) {
            buf.append(format("There are no obvious exits from here.%n"));
        } else {
            if (exits.size() == 1) {
                buf.append(format("There is a single exit to the %s%n", exits.get(0).getDescription()));
            } else {
                String exitsString = String.join(", ", exits.stream().map(Room.Exit::getDescription).collect(Collectors.toList()));
                buf.append(format("There are %d obvious exits: %s%n", exits.size(), exitsString));
            }
        }
        print(buf.toString());
    }

    @Override
    public void inventory(Set<Item> itemsCarried) {
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
