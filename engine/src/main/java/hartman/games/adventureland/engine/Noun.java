
package hartman.games.adventureland.engine;

import java.util.List;
import java.util.Objects;

public class Noun {
    public static final Noun UNRECOGNIZED = new Noun("Unrecognized");

    private String name;
    private List<Noun> synonyms;

    public Noun(String name) {
        this.name = name;
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
        return Objects.equals(name, noun.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
