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

    private final Set<Word> verbs = new LinkedHashSet<>();
    private final Set<Word> words = new LinkedHashSet<>();

    public Vocabulary(Set<Word> verbs, Set<Word> nouns) {
        this.verbs.addAll(filterVerbs(verbs));
        this.words.addAll(filterNouns(nouns));
    }

    private Set<Word> filterVerbs(Set<Word> verbs) {
        return verbs.stream()
                .filter(verb -> !(verb.equals(Word.UNRECOGNIZED) || verb.equals(Word.NONE) || verb.equals(Word.ANY)))
                .collect(Collectors.toSet());
    }

    private Set<Word> filterNouns(Set<Word> words) {
        return words.stream()
                .filter(noun -> !(noun.equals(Word.UNRECOGNIZED) || noun.equals(Word.NONE) || noun.equals(Word.ANY)))
                .collect(Collectors.toSet());
    }

    public Optional<Word> findMatchingVerb(Word verb) {
        return verbs.stream()
                .filter(v -> v.matches(verb))
                .findFirst();
    }

    public Optional<Word> findMatchingNoun(Word word) {
        return words.stream()
                .filter(n -> n.matches(word))
                .findFirst();
    }

    public Vocabulary merge(Vocabulary vocab) {
        Set<Word> mergedVerbSet = new LinkedHashSet<>(this.verbs);
        mergedVerbSet.addAll(vocab.verbs);
        Set<Word> mergedWordSet = new LinkedHashSet<>(this.words);
        mergedWordSet.addAll(vocab.words);
        return new Vocabulary(mergedVerbSet, mergedWordSet);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Vocabulary.class.getSimpleName() + "[", "]")
                .add("verbs=" + verbs)
                .add("nouns=" + words)
                .toString();
    }
}
