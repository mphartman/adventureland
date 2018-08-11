package hartman.games.adventureland.engine;

public class Verb extends Term {
    public static final Verb UNRECOGNIZED = new Verb("Unrecognized");
    public static final Verb ANY = new Verb("");
    public static final Verb NONE = new Verb("");

    public Verb(String name, String... synonyms) {
        super(name, synonyms);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if (this == NONE || o == NONE) return false;

        if (this == ANY || o == ANY) return true;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}