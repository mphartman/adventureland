package hartman.games.adventureland.engine;

import java.util.Objects;

/**
 * The player's desired action represented by a two-word phrase.
 */
public class Command {
    public static final Command NONE = new Command(Word.NONE, Word.NONE);

    private final Word first;
    private final Word second;

    public Command(Word first, Word second) {
        Objects.requireNonNull(first, "First word cannot be null.");
        Objects.requireNonNull(second, "Second word cannot be null.");
        this.first = first;
        this.second = second;
    }

    public Word getSecondThenFirst() {
        return !second.equals(Word.NONE) ? second : first;
    }

    public Word getFirstWord() {
        return first;
    }

    public Word getSecondWord() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        if (!first.equals(command.first)) return false;
        return second.equals(command.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }
}
