package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The player's desired action represented by a two-word phrase.
 */
public class Command {
    public static final Command NONE = new Command();

    private final LinkedList<Word> words = new LinkedList<>();

    private Command() {
        // internal use only
    }

    /**
     * Creates a new command from the given list of words.
     *
     * @throws IllegalArgumentException if wordList is empty or all elements are null
     */
    public Command(Word... wordList) {
        if (wordList.length == 0) {
            throw new IllegalArgumentException("Must have at least one word.");
        }
        this.words.addAll(Arrays.stream(wordList).filter(Objects::nonNull).collect(Collectors.toList()));
        if (this.words.isEmpty()) {
            throw new IllegalArgumentException("Words must contain as least one non-null value.");
        }
    }

    /**
     * Return the word at the given position, otherwise Word.NONE
     */
    public Word getWordOrNone(int position) {
        return getWord(position).orElse(Word.NONE);
    }

    /**
     * Return the word from this Command's word list at the given element position.
     * Position 1 represents the first word, 2 represents the second, and so on.
     */
    public Optional<Word> getWord(int position) {
        if (position > 0 && position <= words.size()) {
            return Optional.of(words.get(position - 1));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return words.stream().map(Word::toString).collect(Collectors.joining(" "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        return words.equals(command.words);
    }

    @Override
    public int hashCode() {
        return words.hashCode();
    }
}
