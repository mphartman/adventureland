package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;

import java.util.List;

public class TestDisplay implements Display {

    private StringBuilder out = new StringBuilder();

    @Override
    public void print(String message) {
        out.append(message);
    }

    @Override
    public void look(Room room, List<Item> itemsInRoom) {
        // do nothing
    }

    @Override
    public void inventory(List<Item> itemsCarried) {
        // do nothing
    }

    @Override
    public String toString() {
        return out.toString();
    }
}
