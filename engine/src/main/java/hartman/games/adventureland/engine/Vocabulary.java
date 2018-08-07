package hartman.games.adventureland.engine;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The language of the adventure. A vocabulary gives the player the options for the verbs and nouns she may use to
 * interact with the game world.
 */
public class Vocabulary {

    private final Set<Verb> verbs = new LinkedHashSet<>();
    private final Set<Noun> nouns = new LinkedHashSet<>();

    public Vocabulary(Set<Verb> verbs, Set<Noun> nouns) {
        this.verbs.addAll(verbs);
        this.nouns.addAll(nouns);
    }

    public Optional<Verb> toVerb(String name) {
        return verbs.stream().filter(v -> v.equals(new Verb(name))).findFirst();
    }

    public Optional<Noun> toNoun(String name) {
        return nouns.stream().filter(n -> n.equals(new Noun(name))).findFirst();
    }
}
