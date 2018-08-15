package hartman.games.adventureland.internal.script;

import hartman.games.adventureland.script.ScriptEventReader;
import hartman.games.adventureland.script.events.RoomEvent;
import hartman.games.adventureland.script.events.ScriptEvent;
import org.junit.Test;

import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScriptEventReaderImplTest {

    @Test
    public void nextReturnsRoomEventGivenNameAndDescription() {
        ScriptEventReader reader = new ScriptEventReaderImpl(new InputStreamReader(getClass().getResourceAsStream("/scripts/001adventure.txt")));
        assertTrue(reader.hasNext());
        ScriptEvent event = reader.next();
        assertNotNull(event);
        assertTrue(event instanceof RoomEvent);
        RoomEvent roomEvent = (RoomEvent) event;
        assertEquals("bedroom", roomEvent.getName());
        assertEquals("A small room with a large bed", roomEvent.getDescription());
        assertFalse(reader.hasNext());
    }

    @Test
    public void nextReturnsRoomEventGivenNameAndDescriptionAndExits() {
        ScriptEventReader reader = new ScriptEventReaderImpl(new InputStreamReader(getClass().getResourceAsStream("/scripts/002adventure.txt")));
        RoomEvent roomEvent;

        roomEvent = (RoomEvent) reader.next();
        assertEquals("bedroom", roomEvent.getName());
        assertEquals("A small room with a large bed", roomEvent.getDescription());

        roomEvent = (RoomEvent) reader.next();
        assertEquals("hallway", roomEvent.getName());
        assertEquals("A short narrow hallway.", roomEvent.getDescription());

    }

}