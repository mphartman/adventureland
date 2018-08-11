package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public abstract class Term {
    private final String name;
    private final Set<String> synonyms = new LinkedHashSet<>();

    public Term(String name, String... synonyms) {
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
                .add("name='" + name + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Term term = (Term) o;

        if (!name.equalsIgnoreCase(term.name)) {
            return synonyms.stream().anyMatch(s -> s.equalsIgnoreCase(term.name));
        }
        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
