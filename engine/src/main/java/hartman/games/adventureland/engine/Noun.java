
package hartman.games.adventureland.engine;

import java.util.*;

public class Noun {
    public static final Noun UNRECOGNIZED = new Noun("Unrecognized");

    private String name;
    private Set<String> synonyms = new LinkedHashSet<>();

    public Noun(String name) {
        this.name = name;
    }

    public Noun(String name, String... synonyms) {
        this.name = name;
        this.synonyms.addAll(Arrays.asList(synonyms));
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Noun noun = (Noun) o;
        boolean nameMatches = Objects.equals(name, noun.name);
        if (!nameMatches) {
            return this.synonyms.contains(noun.name);
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
