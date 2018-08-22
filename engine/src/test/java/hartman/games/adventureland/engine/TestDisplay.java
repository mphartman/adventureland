package hartman.games.adventureland.engine;

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

    public void reset() {
        out.setLength(0);
    }

    @Override
    public String toString() {
        return out.toString();
    }
}
