package hartman.games.adventureland.engine;

import java.util.Set;

public class TestDisplay implements Display {

    private StringBuilder out = new StringBuilder();

    @Override
    public void print(String message) {
        out.append(message);
    }

    @Override
    public void look(Room room, Set<Item> itemsInRoom) {
        // do nothing
    }

    @Override
    public void inventory(Set<Item> itemsCarried) {
        // do nothing
    }

    @Override
    public String toString() {
        return out.toString();
    }
}
