package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Verb {
    public static final Verb UNRECOGNIZED = new Verb("Unrecognized");

    private final String name;
    private final Set<String> synonyms = new LinkedHashSet<>();

    public Verb(String name) {
        this.name = name;
    }

    public Verb(String name, String... synonyms) {
        this.name = name;
        this.synonyms.addAll(Arrays.asList(synonyms));
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Verb verb = (Verb) o;
        boolean nameMatches = Objects.equals(name, verb.name);
        if (!nameMatches) {
            return this.synonyms.contains(verb.name);
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}