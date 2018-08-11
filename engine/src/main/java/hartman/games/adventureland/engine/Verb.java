package hartman.games.adventureland.engine;

public class Verb extends Term {
    public static final Verb UNRECOGNIZED = new Verb("Unrecognized");
    public static final Verb ANY = new Verb("*");
    public static final Verb NONE = new Verb("nil");

    public Verb(String name, String... synonyms) {
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