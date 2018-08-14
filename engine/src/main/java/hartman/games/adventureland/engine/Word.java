package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Word {
    public static final Word UNRECOGNIZED = new Word("Unrecognized");
    public static final Word ANY = new Word("");
    public static final Word NONE = new Word("nil");

    private final String name;
    private final Set<String> synonyms = new LinkedHashSet<>();

    public Word(String name, String... synonyms) {
        Objects.requireNonNull(name, "name cannot be null");
        this.name = name;
        this.synonyms.add(name.toUpperCase());
        this.synonyms.addAll(Arrays.stream(synonyms).map(String::toUpperCase).collect(Collectors.toSet()));
    }

    public String getName() {
        return name;
    }

    public boolean matches(Word that) {
        if (equals(that)) return true;
        if (this == NONE && that == NONE) return true;
        if (this == NONE || that == NONE) return false;
        if (this == ANY || that == ANY) return true;
        Set<String> intersection = new LinkedHashSet<>(synonyms);
        intersection.retainAll(that.synonyms);
        return !intersection.isEmpty();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add(name)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word = (Word) o;

        return name.equals(word.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
