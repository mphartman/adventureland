package hartman.games.adventureland.engine;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The language of the adventure.
 *
 * A vocabulary gives the player the options for the words she may use to interact with the game world.
 */
public class Vocabulary {

    private final Set<Word> words = new LinkedHashSet<>();

    public Vocabulary(Set<Word> words) {
        this.words.addAll(filter(words));
    }

    private Set<Word> filter(Set<Word> words) {
        return words.stream()
                .filter(word -> !(word == null
                        || word.matches(Word.unrecognized())
                        || word.equals(Word.NONE)
                        || word.equals(Word.ANY)))
                .collect(Collectors.toSet());
    }

    public Optional<Word> findMatch(String word) {
        return findMatch(Word.of(word));
    }

    public Optional<Word> findMatch(Word word) {
        return words.stream()
                .filter(n -> n.matches(word))
                .findFirst();
    }

    public Vocabulary merge(Vocabulary vocab) {
        Set<Word> mergedWordSet = new LinkedHashSet<>(this.words);
        mergedWordSet.addAll(vocab.words);
        return new Vocabulary(mergedWordSet);
    }

}
