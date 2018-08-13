package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Noun {
    public static final Noun UNRECOGNIZED = new Noun("Unrecognized");
    public static final Noun ANY = new Noun("");
    public static final Noun NONE = new Noun("nil");

    private final String name;
    private final Set<String> synonyms = new LinkedHashSet<>();

    public Noun(String name, String... synonyms) {
        Objects.requireNonNull(name, "name cannot be null");
        this.name = name;
        this.synonyms.add(name.toUpperCase());
        this.synonyms.addAll(Arrays.stream(synonyms).map(String::toUpperCase).collect(Collectors.toSet()));
    }

    public String getName() {
        return name;
    }

    public boolean matches(Noun that) {
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

        Noun noun = (Noun) o;

        return name.equals(noun.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
