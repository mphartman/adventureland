package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The language of the adventure. A vocabulary gives the player the options for the verbs and nouns she may use to
 * interact with the game world.
 */
public class Vocabulary {

    private final Set<Verb> verbs = new LinkedHashSet<>();
    private final Set<Noun> nouns = new LinkedHashSet<>();

    public static Vocabulary merge(Vocabulary... vocabularies) {
        Set<Verb> verbs = Arrays.stream(vocabularies).map(v -> v.verbs).flatMap(Set::stream).collect(Collectors.toSet());
        Set<Noun> nouns = Arrays.stream(vocabularies).map(v -> v.nouns).flatMap(Set::stream).collect(Collectors.toSet());
        return new Vocabulary(verbs, nouns);
    }

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

    public Vocabulary merge(Vocabulary vocab) {
        Set<Verb> verbs = new LinkedHashSet<>(this.verbs);
        verbs.addAll(vocab.verbs);
        Set<Noun> nouns = new LinkedHashSet<>(this.nouns);
        nouns.addAll(vocab.nouns);
        return new Vocabulary(verbs, nouns);
    }
}
