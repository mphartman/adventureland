package hartman.games.adventureland.script;

import java.io.Reader;

public interface ScriptInputFactory {

    ScriptEventReader createScriptEventReader(Reader reader);
}
