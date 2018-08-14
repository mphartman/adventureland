package hartman.games.adventureland.internal.script;

import hartman.games.adventureland.script.ScriptEventReader;
import hartman.games.adventureland.script.events.RoomEvent;
import hartman.games.adventureland.script.events.ScriptEvent;

import java.io.Reader;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ScriptEventReaderImpl implements ScriptEventReader {
    private Scanner scanner;
    private ScriptEvent event = null;

    private Pattern lineWithQuotes = Pattern.compile("[^\"\\s]+|\"(\\\\.|[^\\\\\"])*\"");

    public ScriptEventReaderImpl(Reader reader) {
        this.scanner = new Scanner(reader);
    }

    @Override
    public void close() {
        scanner.close();
    }

    @Override
    public Iterator<ScriptEvent> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return peek();
    }

    private boolean peek() {
        event = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) break;
            if (line.startsWith("room")) event = doRoom(line);
        }
        return event != null;
    }

    private RoomEvent doRoom(String line) {
        RoomEventImpl roomEvent = null;
        try (Scanner roomScanner = new Scanner(line)) {
            roomScanner.next(); // discard
            if (roomScanner.hasNext(lineWithQuotes)) {
                roomEvent = new RoomEventImpl();
                roomEvent.setName(roomScanner.findInLine(lineWithQuotes));
                String description = roomScanner.findInLine(lineWithQuotes);
                if (description != null) {
                    description = description.replaceAll("\"", "");
                    roomEvent.setDescription(description);
                }
            }
        }
        return roomEvent;
    }

    @Override
    public ScriptEvent next() {
        return event;
    }
}
