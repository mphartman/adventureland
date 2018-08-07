package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Verb {
    public static final Verb UNRECOGNIZED = new Verb("Unrecognized");
    public static final Verb ANY = new Verb("ANY");
    public static final Verb NONE = new Verb("");

    private final String name;
    private final Set<String> synonyms = new LinkedHashSet<>();

    public Verb(String name) {
        this(name, "ANY");
    }

    public Verb(String name, String... synonyms) {
        Objects.requireNonNull(name, "name cannot be null");
        this.name = name;
        this.synonyms.addAll(Arrays.asList(synonyms));
        this.synonyms.add("ANY");
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Verb{" + "name='" + name + '\'' + ", synonyms=" + synonyms + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if (this == ANY) return true;

        Verb verb = (Verb) o;

        if (!name.equalsIgnoreCase(verb.name)) {
            return synonyms.stream().anyMatch(s -> s.equalsIgnoreCase(verb.name));
        }
        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}