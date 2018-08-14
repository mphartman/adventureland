package hartman.games.adventureland.internal.script;

import hartman.games.adventureland.script.ScriptEventReader;
import hartman.games.adventureland.script.ScriptInputFactory;

import java.io.Reader;

public class ScriptInputFactoryImpl implements ScriptInputFactory {
    @Override
    public ScriptEventReader createScriptEventReader(Reader reader) {
        return new ScriptEventReaderImpl(reader);
    }
}
