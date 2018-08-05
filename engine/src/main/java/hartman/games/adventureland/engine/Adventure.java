package hartman.games.adventureland.engine;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A collection of rooms and actions which make up the adventure story.
 */
public class Adventure {
    private final Vocabulary vocabulary;
    private final Set<Room> rooms = new LinkedHashSet<>();
    private final Set<Action> actions = new LinkedHashSet<>();

    public Adventure(Vocabulary vocabulary, Set<Room> rooms, Set<Action> actions) {
        this.vocabulary = vocabulary;
        this.rooms.addAll(rooms);
        this.actions.addAll(actions);
    }

    public Set<Action> getActions() {
        return Collections.unmodifiableSet(actions);
    }
}
