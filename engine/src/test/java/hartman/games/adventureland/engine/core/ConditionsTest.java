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
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.DOWN);
        assertFalse(HAS_EXIT.matches(playerCommand, gameState));
        playerCommand = new PlayerCommand(Verbs.GO, Nouns.UP);
        assertFalse(HAS_EXIT.matches(playerCommand, gameState));
    }

    @Test
    public void hasExitShouldReturnTrueWhenRoomHasRequestedExit() {
        Room end = new Room("end", "end");
        Room start = new Room("start", "start", new Room.Exit.Builder().exit(Nouns.DOWN).towards(end).build());
        GameState gameState = new GameState(start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.DOWN);
        assertTrue(HAS_EXIT.matches(playerCommand, gameState));
        playerCommand = new PlayerCommand(Verbs.GO, Nouns.UP);
        assertFalse(HAS_EXIT.matches(playerCommand, gameState));
    }

    @Test
    public void inRoomShouldReturnFalseWhenPlayerIsNotInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition inRoom = new IN_ROOM(Room.NOWHERE);
        assertFalse(inRoom.matches(playerCommand, gameState));
    }

    @Test
    public void inRoomShouldReturnTrueWhenPlayerIsInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition inRoom = new IN_ROOM(start);
        assertTrue(inRoom.matches(playerCommand, gameState));
    }

    @Test
    public void itemCarriedShouldReturnFalseWhenPlayerInventoryDoesNotHaveItem() {
        Item dagger = Item.newPortableObjectItem("dagger", "A dull, chipped blade.");
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition itemCarried = new ITEM_CARRIED(dagger);
        assertFalse(itemCarried.matches(playerCommand, gameState));
    }

    @Test
    public void itemCarriedShouldReturnTrueWhenPlayerInventoryHasItem() {
        Item torch = Item.newInventoryItem("torch", "An unlit wooden torch dipped in pitch.");
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition itemCarried = new ITEM_CARRIED(torch);
        assertTrue(itemCarried.matches(playerCommand, gameState));
    }

    @Test
    public void itemHereShouldReturnTrueWhenItemIsInRoom() {
        Room entryway = new Room("entryway", "A dark, narrow entry way into the house.");
        Item dog = Item.newSceneryRoomItem("dog", "A large, rapid dog growls at me.", entryway);
        GameState gameState = new GameState(entryway);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition itemHere = new ITEM_HERE(dog);
        assertTrue(itemHere.matches(playerCommand, gameState));
    }

    @Test
    public void itemHereShouldReturnFalseWhenItemIsNotInRoom() {
        Room bathroom = new Room("bathroom", "A luxurious master bathroom with a claw-foot tub.");
        Item microwave = Item.newSceneryRoomItem("microwave", "A 1200-watt microwave.");
        GameState gameState = new GameState(bathroom);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition itemHere = new ITEM_HERE(microwave);
        assertFalse(itemHere.matches(playerCommand, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInRoom() {
        Room doghouse = new Room("doghouse", "A cozy, warm kennel.");
        Item dog = Item.newSceneryRoomItem("dog", "A small sleeps here.", doghouse);
        GameState gameState = new GameState(doghouse);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition isPresent = new IS_PRESENT(dog);
        assertTrue(isPresent.matches(playerCommand, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInInventory() {
        Item key = Item.newInventoryItem("key", "A tarnished brass skeleton key.");
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition isPresent = new IS_PRESENT(key);
        assertTrue(isPresent.matches(playerCommand, gameState));
    }

    @Test
    public void isPresentShouldReturnFalseWhenItemIsNeitherInInventoryOrInRoom() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = Item.newPortableObjectItem("key", "A small key.");
        GameState gameState = new GameState(cell);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.NORTH);
        Condition isPresent = new IS_PRESENT(key);
        assertFalse(isPresent.matches(playerCommand, gameState));
    }

    @Test
    public void notShouldReturnLogicalComplementOfGivenCondition() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = Item.newPortableObjectItem("key", "A small key.", cell);
        GameState gameState = new GameState(cell);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.NORTH);
        Condition isPresent = new IS_PRESENT(key);
        Condition isNotPresent = new NOT(isPresent);
        assertEquals(!isPresent.matches(playerCommand, gameState), isNotPresent.matches(playerCommand, gameState));
    }

    @Test
    public void occursRandomlyShouldReturnTrueGiven100PercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verb.ANY, Noun.UNRECOGNIZED);
        Condition occurs = new OCCURS_RANDOMLY(100);
        assertTrue(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(100, () -> 0);
        assertTrue(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(100, () -> 50);
        assertTrue(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(100, () -> 100);
        assertTrue(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(100, () -> -1);
        assertTrue(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(100, () -> 101);
        assertTrue(occurs.matches(playerCommand, gameState));
    }

    @Test
    public void occursRandomlyShouldReturnFalseGivenZeroPercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verb.ANY, Noun.UNRECOGNIZED);
        Condition occurs = new OCCURS_RANDOMLY(0);
        assertFalse(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(0, () -> 0);
        assertFalse(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(0, () -> 50);
        assertFalse(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(0, () -> 100);
        assertFalse(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(0, () -> -1);
        assertFalse(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(0, () -> 101);
        assertFalse(occurs.matches(playerCommand, gameState));
    }

    @Test
    public void occursRandomlyShouldReturnTrueGivenSuppliedNumberIsLessThenProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verb.ANY, Noun.UNRECOGNIZED);
        Condition occurs = new OCCURS_RANDOMLY(25, () -> 24);
        assertTrue(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(25, () -> 0);
        assertTrue(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(25, () -> 50);
        assertFalse(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(25, () -> 100);
        assertFalse(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(25, () -> -1);
        assertTrue(occurs.matches(playerCommand, gameState));
        occurs = new OCCURS_RANDOMLY(25, () -> 101);
        assertFalse(occurs.matches(playerCommand, gameState));
    }

    @Test
    public void itemMovedShouldReturnTrueIfItemsCurrentLocationDoesNotMatchItsStartingLocation() {
        Item item = Item.newPortableObjectItem("chalice", "A jewel-encrusted golden chalice.");
        PlayerCommand playerCommand = new PlayerCommand(new Verb("PICKUP"), new Noun("chalice"));
        GameState gameState = new GameState(Room.NOWHERE);
        Condition itemMoved = new ITEM_MOVED(item);
        assertFalse(itemMoved.matches(playerCommand, gameState));
        item.stow();
        assertTrue(itemMoved.matches(playerCommand, gameState));
        item.drop(Room.NOWHERE);
        assertFalse(itemMoved.matches(playerCommand, gameState));
    }
}
