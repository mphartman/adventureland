package hartman.games.adventureland.internal.script;

import hartman.games.adventureland.script.ScriptEventReader;
import hartman.games.adventureland.script.events.RoomEvent;
import hartman.games.adventureland.script.events.ScriptEvent;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class ScriptEventReaderImpl implements ScriptEventReader {

    private List<ScriptEvent> events;
    private Scanner scanner;
    private Pattern lineWithQuotes = Pattern.compile("[^\"\\s]+|\"(\\\\.|[^\\\\\"])*\"");
    private int cursor;

    public ScriptEventReaderImpl(Reader reader) {
        this.scanner = new Scanner(reader);
    }

    @Override
    public Iterator<ScriptEvent> iterator() {
        return events.iterator();
    }

    @Override
    public void forEach(Consumer<? super ScriptEvent> action) {
        events.forEach(action);
    }

    @Override
    public Spliterator<ScriptEvent> spliterator() {
        return events.spliterator();
    }

    @Override
    public boolean hasNext() {
        if (events == null) {
            parse();
        }
        return cursor < events.size();
    }

    @Override
    public ScriptEvent next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return events.get(cursor++);
    }

    private List<String> KEYWORDS = asList( "room" );

    private void parse() {
        this.events = new ArrayList<>();
        this.cursor = 0;

        while (scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }

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

    interface ScriptEventBuilder {
        boolean accept(String line);

        void start(String keyword);

        ScriptEvent build();
    }


}
