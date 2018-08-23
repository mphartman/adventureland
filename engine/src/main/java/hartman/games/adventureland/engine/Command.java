package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The player's desired action represented by a two-word phrase.
 */
public class Command {
    public static final Command NONE = new Command(Word.NONE, Word.NONE);

    private final LinkedList<Word> words = new LinkedList<>();

    public Command(Word... wordList) {
        if (wordList.length == 0) {
            throw new IllegalArgumentException("Must have at least one word.");
        }
        this.words.addAll(Arrays.stream(wordList).filter(Objects::nonNull).collect(Collectors.toList()));
        if (this.words.isEmpty()) {
            throw new IllegalArgumentException("Words must contain as least one non-null value.");
        }
    }

    public Command(Word first, Word second) {
        this(new Word[]{first, second});
    }

    public Command(Word first) {
        this(new Word[]{first, Word.NONE});
    }

    public Word getSecondThenFirst() {
        return getSecondWord().equals(Word.NONE) ? getFirstWord() : getSecondWord();
    }

    public Word getFirstWord() {
        return words.getFirst();
    }

    public Word getSecondWord() {
        return words.size() > 1 ? words.get(1) : Word.NONE;
    }

    @Override
    public String toString() {
        return String.format("%s %s", getFirstWord().getName(), getSecondWord().getName());
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
