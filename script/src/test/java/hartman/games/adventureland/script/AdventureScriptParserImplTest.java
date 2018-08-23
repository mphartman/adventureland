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

    @Test
    @AdventureScriptResource("/scripts/030adventure.txt")
    public void exitDirectsBackOnSelfGivenNoRoomName() {
        Adventure adventure = adventureScriptParsingRule.parse();
        Room room = adventure.getStartRoom();
        assertTrue(room.hasExit(new Word("foobar")));
        assertEquals(room, room.exit(new Word("foobar")));
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/040adventure.txt")
    public void badExitRoom() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/060adventure.txt")
    public void duplicateExitDirectionLastOneWins() {
        Adventure adventure = adventureScriptParsingRule.parse();
        Room expectedSwamp = adventure.getStartRoom().exit(new Word("North", "N"));
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
    public void wordGroupMustHaveWord() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/201adventure.txt")
    public void wordGroupCreatesWordWithAliases() {
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
    @AdventureScriptResource("/scripts/306adventure.txt")
    public void actionWordListCreatesAnyMatchingCondition() {
        Adventure adventure = adventureScriptParsingRule.parse();

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(Room.NOWHERE);
        TestDisplay display = new TestDisplay();
        action.run(gameState, display, new Command(new Word("first")));

        assertEquals(String.format("matched%n"), display.toString());
        display.reset();

        action.run(gameState, display, new Command(new Word("nope")));
        assertTrue(display.toString().isEmpty());
        display.reset();

        action.run(gameState, display, new Command(new Word("second")));
        assertEquals(String.format("matched%n"), display.toString());
        display.reset();

        action.run(gameState, display, new Command(new Word("third")));
        assertEquals(String.format("matched%n"), display.toString());
    }

    @Test
    @AdventureScriptResource("/scripts/307adventure.txt")
    public void actionSecondWordListCreatesAnyMatchingCondition() {
        Adventure adventure = adventureScriptParsingRule.parse();

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(Room.NOWHERE);
        TestDisplay display = new TestDisplay();

        action.run(gameState, display, new Command(new Word("first"), new Word("one")));
        assertEquals(String.format("matched!%n"), display.toString());
        display.reset();

        action.run(gameState, display, new Command(new Word("first"), new Word("four")));
        assertTrue(display.toString().isEmpty());
        display.reset();

        action.run(gameState, display, new Command(new Word("first"), new Word("two")));
        assertEquals(String.format("matched!%n"), display.toString());
        display.reset();

        action.run(gameState, display, new Command(new Word("first"), new Word("three")));
        assertEquals(String.format("matched!%n"), display.toString());
    }

    @Test
    @AdventureScriptResource("/scripts/308adventure.txt")
    public void actionFirstWordListAndSecondWordListCreatesAnyMatchingCondition() {
        Adventure adventure = adventureScriptParsingRule.parse();

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(Room.NOWHERE);
        TestDisplay display = new TestDisplay();

        action.run(gameState, display, new Command(new Word("first"), new Word("one")));
        assertEquals(String.format("matched!%n"), display.toString());
        display.reset();

        action.run(gameState, display, new Command(new Word("first"), new Word("two")));
        assertEquals(String.format("matched!%n"), display.toString());
        display.reset();

        action.run(gameState, display, new Command(new Word("second"), new Word("one")));
        assertEquals(String.format("matched!%n"), display.toString());
        display.reset();

        action.run(gameState, display, new Command(new Word("second"), new Word("two")));
        assertEquals(String.format("matched!%n"), display.toString());
        display.reset();

        action.run(gameState, display, new Command(new Word("3"), new Word("one")));
        assertTrue(display.toString().isEmpty());
        display.reset();

        action.run(gameState, display, new Command(new Word("one"), new Word("3")));
        assertTrue(display.toString().isEmpty());
        display.reset();
    }

    @Test
    @AdventureScriptResource("/scripts/312adventure.txt")
    public void actionWordsRecognizeAllWords() {
        Adventure adventure = adventureScriptParsingRule.parse();

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(Room.NOWHERE);
        Display display = new TestDisplay();

        action.run(gameState, display, new Command(new Word("throw"), new Word("axe")));
        assertNotEquals(String.format("Missed!%n"), display.toString());

        action.run(gameState, display, new Command(new Word("throw"), new Word("axe"), new Word("at"), new Word("bear")));
        assertEquals(String.format("Missed!%n"), display.toString());
    }

    @Test
    @AdventureScriptResource("/scripts/302adventure.txt")
    public void actionVerbAndPrintResult() {
        Adventure adventure = adventureScriptParsingRule.parse();

        assertFalse(adventure.getActions().isEmpty());

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(Room.NOWHERE);
        Display display = new TestDisplay();

        action.run(gameState, display, new Command(new Word("print"), Word.NONE));
        assertEquals("It works" + System.getProperty("line.separator"), display.toString());
    }

    @Test
    @AdventureScriptResource("/scripts/303adventure.txt")
    public void actionVerbAndLookResult() {
        Adventure adventure = adventureScriptParsingRule.parse();

        assertFalse(adventure.getActions().isEmpty());

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(Room.NOWHERE);
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
        lair.setExit(new Word("North", "N"), hall);

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(lair);
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

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(Room.NOWHERE);
        Display display = new TestDisplay();

        action.run(gameState, display, new Command(new Word("take"), new Word("poison")));
        assertTrue(gameState.isRunning());

        action.run(gameState, display, new Command(new Word("quit"), Word.NONE));
        assertFalse(gameState.isRunning());
    }

    @Test
    @AdventureScriptResource("/scripts/309adventure.txt")
    public void actionVerbAndSwapItemResult() {
        Adventure adventure = adventureScriptParsingRule.parse();

        Action action = adventure.getActions().iterator().next();
        GameState gameState = new GameState(adventure.getStartRoom());
        TestDisplay display = new TestDisplay();

        Item locked_door = getItemOrFail(adventure.getItems(), "locked_door");
        assertTrue(locked_door.isHere(adventure.getStartRoom()));
        Item open_door = getItemOrFail(adventure.getItems(), "open_door");
        assertTrue(open_door.isHere(Room.NOWHERE));

        action.run(gameState, display, new Command(new Word("unlock"), new Word("door")));
        assertTrue(locked_door.isHere(Room.NOWHERE));
        assertTrue(open_door.isHere(adventure.getStartRoom()));
    }

    @Test
    @AdventureScriptResource("/scripts/310adventure.txt")
    public void actionVerbAndSetFlagResult() {
        Adventure adventure = adventureScriptParsingRule.parse();

        Action action = adventure.getActions().iterator().next();
        GameState gameState = new GameState(adventure.getStartRoom());
        TestDisplay display = new TestDisplay();

        assertFalse(gameState.getFlag("wet"));

        action.run(gameState, display, new Command(new Word("swim"), new Word("underwater")));
        assertTrue(gameState.getFlag("wet"));
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/350adventure.txt")
    public void actionInRoomConditionRequiresRoomToExist() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/351adventure.txt")
    public void actionInRoomCondition() {
        Adventure adventure = adventureScriptParsingRule.parse();

        assertFalse(adventure.getActions().isEmpty());
        assertEquals(new Room("ledge", "A ledge"), adventure.getStartRoom());

        Room ledge = adventure.getStartRoom();

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(ledge);
        TestDisplay display = new TestDisplay();

        action.run(gameState, display, new Command(new Word("climb"), new Word("down")));

        assertEquals("I'm too heavy. I fall!" + System.getProperty("line.separator"), display.toString());
        assertFalse(gameState.isRunning());
    }

    @Test
    @AdventureScriptResource("/scripts/351adventure.txt")
    public void actionInRoomConditionNegativeTest() {
        Adventure adventure = adventureScriptParsingRule.parse();

        Action action = adventure.getActions().iterator().next();

        GameState gameState = new GameState(Room.NOWHERE);
        TestDisplay display = new TestDisplay();

        // not in 'ledge' room, so action condition should not trigger result
        action.run(gameState, display, new Command(new Word("climb"), new Word("down")));

        assertEquals("", display.toString());
        assertTrue(gameState.isRunning());
    }

    @Test
    @AdventureScriptResource("/scripts/352adventure.txt")
    public void actionNotInRoomCondition() {
        Adventure adventure = adventureScriptParsingRule.parse();
        GameState gameState = new GameState(Room.NOWHERE);
        TestDisplay display = new TestDisplay();
        Action action = adventure.getActions().iterator().next();
        action.run(gameState, display, new Command(new Word("climb"), new Word("down")));
        assertEquals("There's nowhere to climb down from here." + System.getProperty("line.separator"), display.toString());
    }

    @Test(expected = IllegalStateException.class)
    @AdventureScriptResource("/scripts/400adventure.txt")
    public void occursRequiresResult() {
        adventureScriptParsingRule.parse();
    }

    @Test
    @AdventureScriptResource("/scripts/401adventure.txt")
    public void occursRunsPrintResult() {
        Adventure adventure = adventureScriptParsingRule.parse();

        assertFalse(adventure.getOccurs().isEmpty());

        GameState gameState = new GameState(Room.NOWHERE);
        TestDisplay display = new TestDisplay();
        Action occurs = adventure.getOccurs().iterator().next();
        occurs.run(gameState, display, Command.NONE);

        assertEquals(String.format("It happened.%n"), display.toString());
    }

    @Test
    @AdventureScriptResource("/scripts/311adventure.txt")
    public void actionPrintResultExpandsEscapedNewline() {
        Adventure adventure = adventureScriptParsingRule.parse();

        TestDisplay testDisplay = new TestDisplay();
        GameState gameState = new GameState(adventure.getStartRoom(), adventure.getItems());
        Action action = adventure.getActions().iterator().next();

        action.run(gameState, testDisplay, new Command(new Word("swim"), new Word("underwater")));

        assertEquals(String.format("I%nam%ndrowning%n...%n...%n...%n"), testDisplay.toString());

    }
}