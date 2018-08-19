package hartman.games.adventureland.engine;

import java.util.List;

/**
 * Presents game output to the user.
 */
public interface Display {

    void print(String message);

    void look(Room room, List<Item> itemsInRoom);

    void inventory(List<Item> itemsCarried);

}
