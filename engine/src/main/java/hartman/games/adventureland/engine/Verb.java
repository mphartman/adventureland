package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class Verb {
    public static final Verb UNRECOGNIZED = new Verb("Unrecognized");
    public static final Verb ANY = new Verb("*");
    public static final Verb NONE = new Verb("nil");

    private final String name;
    private final Set<String> synonyms = new LinkedHashSet<>();

    public Verb(String name, String... synonyms) {
        Objects.requireNonNull(name, "name cannot be null");
        this.name = name;
        this.synonyms.addAll(Arrays.asList(synonyms));
    }

    public String getName() {
        return name;
    }

    public boolean matches(Verb that) {
        if (equals(that)) return true;
        if (this == NONE && that == NONE) return true;
        if (this == NONE || that == NONE) return false;
        if (this == ANY || that == ANY) return true;
        if (name.equalsIgnoreCase(that.name)) return true;
        return synonyms.stream().anyMatch(s -> s.equalsIgnoreCase(that.name));
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

        Verb v = (Verb) o;

        return name.equals(v.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}