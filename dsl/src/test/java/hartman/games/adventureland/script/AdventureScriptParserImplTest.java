package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import hartman.games.adventureland.engine.core.Words;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/100adventure.txt")
    public void itemMustHaveDescription() {
        adventureScriptParsingRule.parse();
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/101adventure.txt")
    public void itemMustHaveName() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/102adventure.txt")
    public void simpleItem() {
        Adventure adventure = adventureScriptParsingRule.parse();
        assertTrue(adventure.getItems().contains(new Item.Builder().named("key").build()));
    }

    @Test
    @AdventureScriptResource("/scripts/103adventure.txt")
    public void itemWithAliases() {
        Adventure adventure = adventureScriptParsingRule.parse();
        Item item = adventure.getItems().stream()
                .filter(i -> i.getName().equals("sword"))
                .findFirst()
                .orElseThrow(AssertionError::new);
        assertTrue(item.isPortable());
        assertTrue(new Item.Builder().named("excalibur").build().matches(item));
        assertTrue(new Item.Builder().named("nightblade").build().matches(item));
        assertTrue(new Item.Builder().named("sharpie").build().matches(item));
    }

    @Test
    @AdventureScriptResource("/scripts/104adventure.txt")
    public void itemsInValidLocations() {
        Adventure adventure = adventureScriptParsingRule.parse();

        Room kitchen = new Room("kitchen", "A kitchen.");

        Item fork = getItemOrFail(adventure.getItems(), "fork");
        assertTrue(fork.isHere(kitchen));

        Item spoon = getItemOrFail(adventure.getItems(), "spoon");
        assertTrue(spoon.isHere(kitchen));

        Item lamp = getItemOrFail(adventure.getItems(), "lamp");
        assertTrue(lamp.isHere(Room.NOWHERE));

        Item knife = getItemOrFail(adventure.getItems(), "knife");
        assertTrue(knife.isHere(Room.NOWHERE));

        Item chest = getItemOrFail(adventure.getItems(), "chest");
        assertTrue(chest.isHere(new Room("hallway", "hallway")));

        Item flintAndSteel = getItemOrFail(adventure.getItems(), "flint");
        assertTrue(flintAndSteel.isCarried());
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/105adventure.txt")
    public void itemMustBeInExistingRoom() {
        adventureScriptParsingRule.parse();
    }

    private Item getItemOrFail(Set<Item> items, String itemName) {
        return items.stream().filter(i -> i.getName().equals(itemName)).findFirst().orElseThrow(AssertionError::new);
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/200adventure.txt")
    public void verbGroupMustHaveVerb() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/201adventure.txt")
    public void verbGroupCreatesVerbWithAliases() {
        Adventure adventure = adventureScriptParsingRule.parse();
        Vocabulary vocabulary = adventure.getVocabulary();

        assertTrue(vocabulary.findMatch("search").isPresent());

        assertTrue(vocabulary.findMatch("go").isPresent());
        assertTrue(vocabulary.findMatch("enter").isPresent());
        assertTrue(vocabulary.findMatch("run").isPresent());
        assertTrue(vocabulary.findMatch("walk").isPresent());

        Word go = vocabulary.findMatch("go").get();
        assertTrue(go.matches(new Word("enter")));
        assertTrue(go.matches(new Word("run")));
        assertTrue(go.matches(new Word("walk")));
        assertFalse(go.matches(new Word("search")));

        Word search = vocabulary.findMatch("search").get();
        assertNotEquals(go, search);
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/202adventure.txt")
    public void nounGroupMustHaveNoun() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/203adventure.txt")
    public void nounGroupCreatesNounWithAliases() {
        Adventure adventure = adventureScriptParsingRule.parse();
        Vocabulary vocabulary = adventure.getVocabulary();

        assertTrue(vocabulary.findMatch("basket").isPresent());

        assertTrue(vocabulary.findMatch("backpack").isPresent());
        assertTrue(vocabulary.findMatch("bag").isPresent());
        assertTrue(vocabulary.findMatch("purse").isPresent());
        assertTrue(vocabulary.findMatch("manpurse").isPresent());

        Word backpack = vocabulary.findMatch("backpack").get();
        assertTrue(backpack.matches(new Word("bag")));
        assertTrue(backpack.matches(new Word("purse")));
        assertTrue(backpack.matches(new Word("manpurse")));
        assertFalse(backpack.matches(new Word("basket")));

        Word basket = vocabulary.findMatch("basket").get();
        assertNotEquals(backpack, basket);
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/300adventure.txt")
    public void actionRequiresVerbAndResult() {
        adventureScriptParsingRule.parse();
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/301adventure.txt")
    public void actionRequiresResult() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/302adventure.txt")
    public void actionVerbAndPrintResult() {
        Adventure adventure = adventureScriptParsingRule.parse();

        assertFalse(adventure.getActions().isEmpty());

        GameState gameState = new GameState(Room.NOWHERE);
        Action action = adventure.getActions().iterator().next();
        Display display = new TestDisplay();
        action.run(gameState, display, new Command(new Word("print"), Word.NONE));
        assertEquals("It works\n", display.toString());
    }

    @Test
    @AdventureScriptResource("/scripts/303adventure.txt")
    public void actionVerbAndLookResult() {
        Adventure adventure = adventureScriptParsingRule.parse();

        assertFalse(adventure.getActions().isEmpty());

        GameState gameState = new GameState(Room.NOWHERE);
        Action action = adventure.getActions().iterator().next();
        Display display = new TestDisplay() {
            @Override
            public void look(Room room, List<Item> itemsInRoom) {
                print("It works");
            }
        };
        action.run(gameState, display, new Command(new Word("look"), Word.NONE));
        assertEquals("It works", display.toString());
    }

    @Test
    @AdventureScriptResource("/scripts/304adventure.txt")
    public void actionVerbAndGoResult() {
        Adventure adventure = adventureScriptParsingRule.parse();

        assertFalse(adventure.getActions().isEmpty());

        Room lair = new Room("lair", "A scary lair");
        Room hall = new Room("hall", "A hall");
        lair.setExit(Words.NORTH, hall);

        GameState gameState = new GameState(lair);
        Action action = adventure.getActions().iterator().next();
        Display display = new TestDisplay();
        action.run(gameState, display, new Command(new Word("flee"), new Word("south")));
        assertEquals(lair, gameState.getCurrentRoom());
        assertNotEquals(hall, gameState.getCurrentRoom());
        action.run(gameState, display, new Command(new Word("flee"), new Word("north")));
        assertEquals(hall, gameState.getCurrentRoom());
    }

    @Test
    @AdventureScriptResource("/scripts/305adventure.txt")
    public void actionVerbAndQuitResult() {
        Adventure adventure = adventureScriptParsingRule.parse();

        assertFalse(adventure.getActions().isEmpty());

        GameState gameState = new GameState(Room.NOWHERE);
        Action action = adventure.getActions().iterator().next();
        Display display = new TestDisplay();
        action.run(gameState, display, new Command(new Word("take"), new Word("poison")));
        assertTrue(gameState.isRunning());
        action.run(gameState, display, new Command(new Word("quit"), Word.NONE));
        assertFalse(gameState.isRunning());
    }

}