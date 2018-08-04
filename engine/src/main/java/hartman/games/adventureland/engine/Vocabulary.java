package hartman.games.adventureland.engine;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The language of the adventure. A vocabulary gives the player the options for the verbs and nouns she may use to
 * interact with the game world.
 */
public class Vocabulary {

    private final Set<Verb> verbs = new LinkedHashSet<>();
    private final Set<Noun> nouns = new LinkedHashSet<>();
    private final Set<Action> actions = new LinkedHashSet<>();

    public Vocabulary(Set<Verb> verbs, Set<Noun> nouns) {
        this.verbs.addAll(verbs);
        this.nouns.addAll(nouns);
    }

    public Vocabulary(Set<Verb> verbs, Set<Noun> nouns, Set<Action> actions) {
        this.verbs.addAll(verbs);
        this.nouns.addAll(nouns);
        this.actions.addAll(actions);
    }
}
