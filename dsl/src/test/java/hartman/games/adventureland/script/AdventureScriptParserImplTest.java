package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Word;
import hartman.games.adventureland.engine.core.Words;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdventureScriptParserImplTest {

    @Rule
    public AdventureScriptParsingRule adventureScriptParsingRule = new AdventureScriptParsingRule(new AdventureScriptParserImpl());

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/000adventure.txt")
    public void emptyScriptThrowsException() {
        adventureScriptParsingRule.parse();
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/001adventure.txt")
    public void roomMissingDescriptionThrowsException() {
        adventureScriptParsingRule.parse();
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/002adventure.txt")
    public void roomMissingNameThrowsException() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/003adventure.txt")
    public void validRoomNames() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/010adventure.txt")
    public void singleRoomNoExits() {
        Adventure adventure = adventureScriptParsingRule.parse();
        Room bedroom = new Room("bedroom", "A small room with a \"large\" bed");
        assertEquals(bedroom, adventure.getStartRoom());
        assertEquals(bedroom.getDescription(), adventure.getStartRoom().getDescription());
    }

    @Test
    @AdventureScriptResource("/scripts/020adventure.txt")
    public void twoRoomsConnectedByExits() {
        Adventure adventure = adventureScriptParsingRule.parse();

        assertEquals(new Room("bedroom", "A small room with a large bed"), adventure.getStartRoom());
        assertEquals("A small room with a large bed", adventure.getStartRoom().getDescription());

        Room bedroom = adventure.getStartRoom();
        assertTrue("bedroom should have north exit", bedroom.hasExit(new Word("north")));

        Room hallway = bedroom.exit(new Word("north"));
        assertEquals("north exit from bedroom should be hallway", new Room("hallway", "A short narrow hallway."), hallway);

        assertTrue("hallway should have south exit", hallway.hasExit(new Word("south")));
        assertTrue("hallway should have north exit", hallway.hasExit(new Word("north")));
        assertEquals("hallway's north exit is to self", hallway, hallway.exit(new Word("north")));
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/030adventure.txt")
    public void badExitDirection() {
        adventureScriptParsingRule.parse();
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/040adventure.txt")
    public void badExitRoom() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/050adventure.txt")
    public void validRoomExitDirectionsAreAccepted() {
        Adventure adventure = adventureScriptParsingRule.parse();
        Room start = adventure.getStartRoom();
        assertTrue(start.hasExit(Words.NORTH));
        assertTrue(start.hasExit(Words.SOUTH));
        assertTrue(start.hasExit(Words.EAST));
        assertTrue(start.hasExit(Words.WEST));
        assertTrue(start.hasExit(Words.UP));
        assertTrue(start.hasExit(Words.DOWN));
    }

    @Test
    @AdventureScriptResource("/scripts/060adventure.txt")
    public void duplicateExitDirectionLastOneWins() {
        Adventure adventure = adventureScriptParsingRule.parse();
        Room expectedSwamp = adventure.getStartRoom().exit(Words.NORTH);
        assertEquals("swamp", expectedSwamp.getName());
    }

    @Test
    @AdventureScriptResource("/scripts/070adventure.txt")
    public void startSpecifiesWhichRoomAdventureStartsIn() {
        Adventure adventure = adventureScriptParsingRule.parse();
        assertEquals(new Room("meadow", "I'm in a beautiful meadow."), adventure.getStartRoom());
    }
}