package hartman.games.adventureland.engine;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Word implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Word UNRECOGNIZED = new Word("<unrecognized>", false);
    public static final Word ANY = new Word("<any>");
    public static final Word NONE = new Word("<none>");

    public static Word of(String name) {
        return new Word(name);
    }

    public static Word unrecognized() {
        return UNRECOGNIZED;
    }

    public static Word unrecognized(String name) {
        return new Word(name, false);
    }

    private final String name;
    private final Set<String> synonyms = new LinkedHashSet<>();
    private final boolean recognized;

    public Word(String name, boolean recognized, String... synonyms) {
        Objects.requireNonNull(name, "name cannot be null");
        this.name = name;
        this.synonyms.add(name.toUpperCase());
        this.synonyms.addAll(Arrays.stream(synonyms).map(String::toUpperCase).collect(Collectors.toSet()));
        this.recognized = recognized;
    }

    public Word(String name, String... synonyms) {
        this(name, true, synonyms);
    }

    public String getName() {
        return name;
    }

    public boolean matches(Word that) {
        if (equals(that)) return true;
        if (this.isUnrecognized() && that.isUnrecognized()) return true;
        if (this == NONE && that == NONE) return true;
        if (this == NONE || that == NONE) return false;
        if (this == ANY || that == ANY) return true;
        Set<String> intersection = new LinkedHashSet<>(synonyms);
        intersection.retainAll(that.synonyms);
        return !intersection.isEmpty();
    }

    public boolean isUnrecognized() {
        return !recognized;
    }

    @Override
    public String toString() {
        if (recognized) {
            return name;
        }
        return String.format("%s‹?›", name);
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
