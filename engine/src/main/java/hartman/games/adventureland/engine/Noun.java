
package hartman.games.adventureland.engine;

public class Noun extends Term {
    public static final Noun UNRECOGNIZED = new Noun("Unrecognized");
    public static final Noun ANY = new Noun("");
    public static final Noun NONE = new Noun("");

    public Noun(String name, String... synonyms) {
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

}
