package hartman.games.adventureland.engine;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A vocabulary, a set of actions, and a collection of game elements like rooms and items which make up the adventure story.
 */
public class Adventure {
    private final Vocabulary vocabulary;
    private final Set<Action> occurs = new LinkedHashSet<>();
    private final Set<Action> actions = new LinkedHashSet<>();
    private final Set<Item> items = new LinkedHashSet<>();
    private final Room startRoom;

    public Adventure(Vocabulary vocabulary, Set<Action> occurs, Set<Action> actions, Set<Item> items, Room startRoom) {
        this.vocabulary = vocabulary;
        this.occurs.addAll(occurs);
        this.actions.addAll(actions);
        this.items.addAll(items);
        this.startRoom = startRoom;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public Set<Action> getOccurs() {
        return Collections.unmodifiableSet(occurs);
    }

    public Set<Action> getActions() {
        return Collections.unmodifiableSet(actions);
    }

    public Set<Item> getItems() {
        return Collections.unmodifiableSet(items);
    }

    public Room getStartRoom() {
        return startRoom;
    }
}
