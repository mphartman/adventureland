package hartman.games.adventureland.engine;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public abstract class Term {
    private final String name;
    private final Set<String> synonyms = new LinkedHashSet<>();

    public Term(String name, String... synonyms) {
        Objects.requireNonNull(name, "name cannot be null");
        this.name = name.toUpperCase();
        this.synonyms.addAll(Arrays.stream(synonyms).map(String::toUpperCase).collect(Collectors.toSet()));
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

    public boolean matches(Term that) {
        if (equals(that)) return true;
        if (!that.getClass().isAssignableFrom(this.getClass())) return false;
        if (name.equals(that.name)) return true;
        return synonyms.stream().anyMatch(s -> s.equals(that.name));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Term term = (Term) o;

        return name.equals(term.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
