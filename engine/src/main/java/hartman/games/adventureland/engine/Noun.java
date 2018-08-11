
package hartman.games.adventureland.engine;

public class Noun extends Term {
    public static final Noun UNRECOGNIZED = new Noun("Unrecognized");
    public static final Noun ANY = new Noun("*");
    public static final Noun NONE = new Noun("nil");

    public Noun(String name, String... synonyms) {
        super(name, synonyms);
    }

    @Override
    public boolean matches(Term that) {
        if (this == NONE && that == NONE) return true;
        if (this == NONE || that == NONE) return false;
        if (this == ANY || that == ANY) return true;
        return super.matches(that);
    }

}
