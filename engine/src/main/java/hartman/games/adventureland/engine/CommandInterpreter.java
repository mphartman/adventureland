package hartman.games.adventureland.engine;

/**
 * An CommandInterpreter parses player input from some device into a {@link Command}.
 */
@FunctionalInterface
public interface CommandInterpreter {

    Command nextCommand();

}
