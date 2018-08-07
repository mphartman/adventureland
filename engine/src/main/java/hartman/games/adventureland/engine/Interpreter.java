package hartman.games.adventureland.engine;

/**
 * An Interpreter parses player input from some device into a {@link PlayerCommand}.
 */
@FunctionalInterface
public interface Interpreter {

    PlayerCommand nextCommand();

}
