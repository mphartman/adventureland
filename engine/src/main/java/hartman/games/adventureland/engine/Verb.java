package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Verb {
    public static final Verb UNRECOGNIZED = new Verb("Unrecognized");
    public static final Verb ANY = new Verb("ANY");

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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