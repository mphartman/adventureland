package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.*;
import hartman.games.adventureland.engine.Action.Condition;
import org.junit.Test;

import static hartman.games.adventureland.engine.core.Conditions.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionsTest {

    @Test
    public void hasExitShouldReturnFalseWhenRoomHasNoExits() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Nouns.DOWN);
        assertFalse(HasExit.matches(command, gameState));
        command = new Command(Verbs.GO, Nouns.UP);
        assertFalse(HasExit.matches(command, gameState));
    }

    @Test
    public void hasExitShouldReturnTrueWhenRoomHasRequestedExit() {
        Room end = new Room("end", "end");
        Room start = new Room("start", "start", new Room.Exit.Builder().exit(Nouns.DOWN).towards(end).build());
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Nouns.DOWN);
        assertTrue(HasExit.matches(command, gameState));
        command = new Command(Verbs.GO, Nouns.UP);
        assertFalse(HasExit.matches(command, gameState));
    }

    @Test
    public void inRoomShouldReturnFalseWhenPlayerIsNotInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition inRoom = new InRoom(Room.NOWHERE);
        assertFalse(inRoom.matches(command, gameState));
    }

    @Test
    public void inRoomShouldReturnTrueWhenPlayerIsInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition inRoom = new InRoom(start);
        assertTrue(inRoom.matches(command, gameState));
    }

    @Test
    public void itemCarriedShouldReturnFalseWhenPlayerInventoryDoesNotHaveItem() {
        Item dagger = Item.newPortableObjectItem("dagger", "A dull, chipped blade.");
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemCarried = new ItemCarried(dagger);
        assertFalse(itemCarried.matches(command, gameState));
    }

    @Test
    public void itemCarriedShouldReturnTrueWhenPlayerInventoryHasItem() {
        Item torch = Item.newInventoryItem("torch", "An unlit wooden torch dipped in pitch.");
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemCarried = new ItemCarried(torch);
        assertTrue(itemCarried.matches(command, gameState));
    }

    @Test
    public void itemHereShouldReturnTrueWhenItemIsInRoom() {
        Room entryway = new Room("entryway", "A dark, narrow entry way into the house.");
        Item dog = Item.newSceneryRoomItem("dog", "A large, rapid dog growls at me.", entryway);
        GameState gameState = new GameState(entryway);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemHere = new ItemHere(dog);
        assertTrue(itemHere.matches(command, gameState));
    }

    @Test
    public void itemHereShouldReturnFalseWhenItemIsNotInRoom() {
        Room bathroom = new Room("bathroom", "A luxurious master bathroom with a claw-foot tub.");
        Item microwave = Item.newSceneryRoomItem("microwave", "A 1200-watt microwave.");
        GameState gameState = new GameState(bathroom);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemHere = new ItemHere(microwave);
        assertFalse(itemHere.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInRoom() {
        Room doghouse = new Room("doghouse", "A cozy, warm kennel.");
        Item dog = Item.newSceneryRoomItem("dog", "A small sleeps here.", doghouse);
        GameState gameState = new GameState(doghouse);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition isPresent = new IsPresent(dog);
        assertTrue(isPresent.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInInventory() {
        Item key = Item.newInventoryItem("key", "A tarnished brass skeleton key.");
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition isPresent = new IsPresent(key);
        assertTrue(isPresent.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnFalseWhenItemIsNeitherInInventoryOrInRoom() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = Item.newPortableObjectItem("key", "A small key.");
        GameState gameState = new GameState(cell);
        Command command = new Command(Verbs.GO, Nouns.NORTH);
        Condition isPresent = new IsPresent(key);
        assertFalse(isPresent.matches(command, gameState));
    }

    @Test
    public void notShouldReturnLogicalComplementOfGivenCondition() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = Item.newPortableObjectItem("key", "A small key.", cell);
        GameState gameState = new GameState(cell);
        Command command = new Command(Verbs.GO, Nouns.NORTH);
        Condition isPresent = new IsPresent(key);
        Condition isNotPresent = new Not(isPresent);
        assertEquals(!isPresent.matches(command, gameState), isNotPresent.matches(command, gameState));
    }

    @Test
    public void occursRandomlyShouldReturnTrueGiven100PercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verb.ANY, Noun.UNRECOGNIZED);
        Condition occurs = new Random(100);
        assertTrue(occurs.matches(command, gameState));
        occurs = new Random(100, () -> 0);
        assertTrue(occurs.matches(command, gameState));
        occurs = new Random(100, () -> 50);
        assertTrue(occurs.matches(command, gameState));
        occurs = new Random(100, () -> 100);
        assertTrue(occurs.matches(command, gameState));
        occurs = new Random(100, () -> -1);
        assertTrue(occurs.matches(command, gameState));
        occurs = new Random(100, () -> 101);
        assertTrue(occurs.matches(command, gameState));
    }

    @Test
    public void randomShouldReturnFalseGivenZeroPercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verb.ANY, Noun.UNRECOGNIZED);
        Condition occurs = new Random(0);
        assertFalse(occurs.matches(command, gameState));
        occurs = new Random(0, () -> 0);
        assertFalse(occurs.matches(command, gameState));
        occurs = new Random(0, () -> 50);
        assertFalse(occurs.matches(command, gameState));
        occurs = new Random(0, () -> 100);
        assertFalse(occurs.matches(command, gameState));
        occurs = new Random(0, () -> -1);
        assertFalse(occurs.matches(command, gameState));
        occurs = new Random(0, () -> 101);
        assertFalse(occurs.matches(command, gameState));
    }

    @Test
    public void randomShouldReturnTrueGivenSuppliedNumberIsLessThenProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verb.ANY, Noun.UNRECOGNIZED);
        Condition occurs = new Random(25, () -> 24);
        assertTrue(occurs.matches(command, gameState));
        occurs = new Random(25, () -> 0);
        assertTrue(occurs.matches(command, gameState));
        occurs = new Random(25, () -> 50);
        assertFalse(occurs.matches(command, gameState));
        occurs = new Random(25, () -> 100);
        assertFalse(occurs.matches(command, gameState));
        occurs = new Random(25, () -> -1);
        assertTrue(occurs.matches(command, gameState));
        occurs = new Random(25, () -> 101);
        assertFalse(occurs.matches(command, gameState));
    }

    @Test
    public void itemMovedShouldReturnTrueIfItemsCurrentLocationDoesNotMatchItsStartingLocation() {
        Item item = Item.newPortableObjectItem("chalice", "A jewel-encrusted golden chalice.");
        Command command = new Command(new Verb("PICKUP"), new Noun("chalice"));
        GameState gameState = new GameState(Room.NOWHERE);
        Condition itemMoved = new ItemMoved(item);
        assertFalse(itemMoved.matches(command, gameState));
        item.stow();
        assertTrue(itemMoved.matches(command, gameState));
        item.drop(Room.NOWHERE);
        assertFalse(itemMoved.matches(command, gameState));
    }
}
