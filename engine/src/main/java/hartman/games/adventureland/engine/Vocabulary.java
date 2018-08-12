package hartman.games.adventureland.engine;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * The language of the adventure. A vocabulary gives the player the options for the verbs and nouns she may use to
 * interact with the game world.
 */
public class Vocabulary {

    private final Set<Verb> verbs = new LinkedHashSet<>();
    private final Set<Noun> nouns = new LinkedHashSet<>();

    public Vocabulary(Set<Verb> verbs, Set<Noun> nouns) {
        this.verbs.addAll(filterVerbs(verbs));
        this.nouns.addAll(filterNouns(nouns));
    }

    private Set<Verb> filterVerbs(Set<Verb> verbs) {
        return verbs.stream()
                .filter(verb -> !(verb.equals(Verb.UNRECOGNIZED) || verb.equals(Verb.NONE) || verb.equals(Verb.ANY)))
                .collect(Collectors.toSet());
    }

    private Set<Noun> filterNouns(Set<Noun> nouns) {
        return nouns.stream()
                .filter(noun -> !(noun.equals(Noun.UNRECOGNIZED) || noun.equals(Noun.NONE) || noun.equals(Noun.ANY)))
                .collect(Collectors.toSet());
    }

    public Optional<Verb>find(Verb verb) {
        return verbs.stream()
                .filter(v -> v.matches(verb))
                .findFirst();
    }

    public Optional<Noun> find(Noun noun) {
        return nouns.stream()
                .filter(n -> n.matches(noun))
                .findFirst();
    }

    public Vocabulary merge(Vocabulary vocab) {
        Set<Verb> mergedVerbSet = new LinkedHashSet<>(this.verbs);
        mergedVerbSet.addAll(vocab.verbs);
        Set<Noun> mergedNounSet = new LinkedHashSet<>(this.nouns);
        mergedNounSet.addAll(vocab.nouns);
        return new Vocabulary(mergedVerbSet, mergedNounSet);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Vocabulary.class.getSimpleName() + "[", "]")
                .add("verbs=" + verbs)
                .add("nouns=" + nouns)
                .toString();
    }
}
