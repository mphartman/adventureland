package hartman.games.adventureland.engine;

/**
 * An Interpreter parses player input from some device into a {@link Command}.
 */
@FunctionalInterface
public interface Interpreter {

    Command nextCommand();

}
