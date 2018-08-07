
package hartman.games.adventureland.engine;

import java.util.*;

public class Noun {
    public static final Noun UNRECOGNIZED = new Noun("Unrecognized");
    public static final Noun ANY = new Noun("ANY");
    public static final Noun NONE = new Noun("");

    private String name;
    private Set<String> synonyms = new HashSet<>();

    public Noun(String name) {
        this(name, "ANY");
    }

    public Noun(String name, String... synonyms) {
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
        return "Noun{" + "name='" + name + '\'' + ", synonyms=" + synonyms + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if (this == ANY) return true;

        Noun noun = (Noun) o;

        if (!name.equalsIgnoreCase(noun.name)) {
            return synonyms.stream().anyMatch(s -> s.equalsIgnoreCase(noun.name));
        }
        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
