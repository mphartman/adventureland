package hartman.games.adventureland.script;

import hartman.games.adventureland.script.events.ScriptEvent;

import java.io.Closeable;
import java.util.Iterator;

public interface ScriptEventReader extends Iterator<ScriptEvent>, Iterable<ScriptEvent>, Closeable {



}
