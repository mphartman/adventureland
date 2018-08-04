package hartman.games.adventureland.engine;

/**
 * Given a Vocabulary, an Interpreter parses player input from
 * some device into a {@link PlayerCommand}.
 */
public interface Interpreter {

    void setVocabulary(Vocabulary vocabulary);

    PlayerCommand parse();

}
