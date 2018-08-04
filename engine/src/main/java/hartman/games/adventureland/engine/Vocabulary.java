package hartman.games.adventureland.engine;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The language of the adventure. A vocabulary gives the player the options for the verbs and nouns she may use to
 * interact with the game world.
 */
public class Vocabulary {

    private final Set<Verb> verbs = new LinkedHashSet<>();
    private final Set<Noun> nouns = new LinkedHashSet<>();
    private final Set<Action> actions = new LinkedHashSet<>();

    public static class Verb {
        public static final Verb UNRECOGNIZED = new Verb("Unrecognized");

        private String name;
        private List<Verb> synonyms;

        public Verb(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Verb{" +
                    "name='" + name + '\'' +
                    ", synonyms=" + synonyms +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Verb verb = (Verb) o;
            return Objects.equals(name, verb.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    public static class Noun {
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
            return "Noun{" +
                    "name='" + name + '\'' +
                    ", synonyms=" + synonyms +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Noun noun = (Noun) o;
            return Objects.equals(name, noun.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    public Vocabulary(Set<Verb> verbs, Set<Noun> nouns) {
        this.verbs.addAll(verbs);
        this.nouns.addAll(nouns);
    }

    public Vocabulary(Set<Verb> verbs, Set<Noun> nouns, Set<Action> actions) {
        this.verbs.addAll(verbs);
        this.nouns.addAll(nouns);
        this.actions.addAll(actions);
    }
}
