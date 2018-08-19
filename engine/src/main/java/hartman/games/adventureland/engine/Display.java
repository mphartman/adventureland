package hartman.games.adventureland.engine;

import java.util.Set;

/**
 * Presents game output to the user.
 */
public interface Display {

    void print(String message);

    void look(Room room, Set<Item> itemsInRoom);

    void inventory(Set<Item> itemsCarried);

}
