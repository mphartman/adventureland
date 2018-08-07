package hartman.games.adventureland.engine;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A collection of rooms and actions which make up the adventure story.
 */
public class Adventure {
    private final Vocabulary vocabulary;
    private final Set<Action> occurs = new LinkedHashSet<>();
    private final Set<Action> actions = new LinkedHashSet<>();
    private final Room startRoom;

    public Adventure(Vocabulary vocabulary, Set<Action> occurs, Set<Action> actions, Room startRoom) {
        this.vocabulary = vocabulary;
        this.actions.addAll(actions);
        this.occurs.addAll(occurs);
        this.startRoom = startRoom;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public Room getStartRoom() {
        return startRoom;
    }

    public Set<Action> getOccurs() {
        return Collections.unmodifiableSet(occurs);
    }

    public Set<Action> getActions() {
        return Collections.unmodifiableSet(actions);
    }
}
