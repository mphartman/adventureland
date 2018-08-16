package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for reading Adventureland adventure scripts.
 */
public interface AdventureScriptParser {

    /**
     * Parse an adventure script.
     *
     * @param is the input stream for the script
     * @return A new instance of an Adventure object
     * @throws IOException If an IO error occurs interacting with the InputStream.
     */
    Adventure parse(InputStream is) throws IOException;

}
