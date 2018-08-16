package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Word;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdventureScriptParserImplTest {

    private Adventure parseAdventure(String path) {
        try {
            try (InputStream inputStream = getClass().getResourceAsStream(path)) {
                AdventureScriptParser parser = new AdventureScriptParserImpl();
                return parser.parse(inputStream);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void emptyScriptThrowsException() {
        parseAdventure("/scripts/000adventure.txt");
    }

    @Test(expected = IllegalStateException.class)
    public void roomMissingDescriptionThrowsException() {
        parseAdventure("/scripts/001adventure.txt");
    }

    @Test(expected = IllegalStateException.class)
    public void roomMissingNameThrowsException() {
        parseAdventure("/scripts/002adventure.txt");
    }

    @Test
    public void singleRoomNoExits() {
        Adventure adventure = parseAdventure("/scripts/010adventure.txt");
        Room bedroom = new Room("bedroom", "A small room with a large bed");
        assertEquals(bedroom, adventure.getStartRoom());
        assertEquals(bedroom.getDescription(), adventure.getStartRoom().getDescription());
    }

    @Test
    @Ignore
    public void twoRoomsConnectedByExits() {
        Adventure adventure = parseAdventure("/scripts/020adventure.txt");
        Room bedroom = new Room("bedroom", "A small room with a large bed");
        assertEquals(bedroom, adventure.getStartRoom());
        assertEquals(bedroom.getDescription(), adventure.getStartRoom().getDescription());

        assertTrue("bedroom should have north exit", bedroom.hasExit(new Word("north")));

        Room hallway = new Room("hallway", "A short narrow hallway.");
        assertEquals("north exit from bedroom should be hallway", hallway, bedroom.exit(new Word("north")));

        assertTrue("hallway should have south exit", hallway.hasExit(new Word("south")));
    }
}