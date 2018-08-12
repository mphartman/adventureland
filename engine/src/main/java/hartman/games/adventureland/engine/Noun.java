
package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class Noun {
    public static final Noun UNRECOGNIZED = new Noun("Unrecognized");
    public static final Noun ANY = new Noun("*");
    public static final Noun NONE = new Noun("nil");

    private final String name;
    private final Set<String> synonyms = new LinkedHashSet<>();

    public Noun(String name, String... synonyms) {
        Objects.requireNonNull(name, "name cannot be null");
        this.name = name;
        this.synonyms.addAll(Arrays.asList(synonyms));
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add(name)
                .toString();
    }

    public boolean matches(Noun that) {
        if (equals(that)) return true;
        if (!that.getClass().isAssignableFrom(this.getClass())) return false;
        if (this == NONE && that == NONE) return true;
        if (this == NONE || that == NONE) return false;
        if (this == ANY || that == ANY) return true;
        if (name.equalsIgnoreCase(that.name)) return true;
        return synonyms.stream().anyMatch(s -> s.equalsIgnoreCase(that.name));
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
