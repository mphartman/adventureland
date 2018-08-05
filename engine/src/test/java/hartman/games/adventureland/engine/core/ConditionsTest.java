package hartman.games.adventureland.engine.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import hartman.games.adventureland.engine.*;
import org.junit.Test;

import hartman.games.adventureland.engine.Action.Condition;

public class ConditionsTest {

    @Test
    public void hasExitShouldReturnFalseWhenRoomHasNoExits() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.DOWN);
        assertFalse(Conditions.HAS_EXIT.apply(playerCommand, gameState));
        playerCommand = new PlayerCommand(Verbs.GO, Nouns.UP);
        assertFalse(Conditions.HAS_EXIT.apply(playerCommand, gameState));
    }

    @Test
    public void hasExitShouldReturnTrueWhenRoomHasRequestedExit() {
        Room end = new Room("end", "end");
        Room start = new Room("start", "start", new Room.Exit.Builder().exit(Nouns.DOWN).towards(end).build());
        GameState gameState = new GameState(start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.DOWN);
        assertTrue(Conditions.HAS_EXIT.apply(playerCommand, gameState));
        playerCommand = new PlayerCommand(Verbs.GO, Nouns.UP);
        assertFalse(Conditions.HAS_EXIT.apply(playerCommand, gameState));
    }

    @Test
    public void inRoomShouldReturnFalseWhenPlayerIsNotInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition inRoom = new Conditions.IN_ROOM(Room.NOWHERE);
        assertFalse(inRoom.apply(playerCommand, gameState));
    }

    @Test
    public void inRoomShouldReturnTrueWhenPlayerIsInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition inRoom = new Conditions.IN_ROOM(start);
        assertTrue(inRoom.apply(playerCommand, gameState));
    }

    @Test
    public void itemCarriedShouldReturnFalseWhenPlayerInventoryDoesNotHaveItem() {
        Item dagger = Item.newPortableObjectItem("dagger", "A dull, chipped blade.");
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition itemCarried = new Conditions.ITEM_CARRIED(dagger);
        assertFalse(itemCarried.apply(playerCommand, gameState));
    }

    @Test
    public void itemCarriedShouldReturnTrueWhenPlayerInventoryHasItem() {
        Item torch = Item.newInventoryItem("torch", "An unlit wooden torch dipped in pitch.");
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition itemCarried = new Conditions.ITEM_CARRIED(torch);
        assertTrue(itemCarried.apply(playerCommand, gameState));
    }

    @Test
    public void itemHereShouldReturnTrueWhenItemIsInRoom() {
        Room entryway = new Room("entryway", "A dark, narrow entry way into the house.");
        Item dog = Item.newSceneryRoomItem("dog", "A large, rapid dog growls at me.", entryway);
        GameState gameState = new GameState(entryway);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition itemHere = new Conditions.ITEM_HERE(dog);
        assertTrue(itemHere.apply(playerCommand, gameState));
    }

    @Test
    public void itemHereShouldReturnFalseWhenItemIsNotInRoom() {
        Room bathroom = new Room("bathroom", "A luxurious master bathroom with a claw-foot tub.");
        Item microwave = Item.newSceneryRoomItem("microwave", "A 1200-watt microwave.");
        GameState gameState = new GameState(bathroom);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition itemHere = new Conditions.ITEM_HERE(microwave);
        assertFalse(itemHere.apply(playerCommand, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInRoom() {
        Room doghouse = new Room("doghouse", "A cozy, warm kennel.");
        Item dog = Item.newSceneryRoomItem("dog", "A small sleeps here.", doghouse);
        GameState gameState = new GameState(doghouse);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition isPresent = new Conditions.IS_PRESENT(dog);
        assertTrue(isPresent.apply(playerCommand, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInInventory() {
        Item key = Item.newInventoryItem("key", "A tarnished brass skeleton key.");
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Noun.ANY);
        Condition isPresent = new Conditions.IS_PRESENT(key);
        assertTrue(isPresent.apply(playerCommand, gameState));
    }

    @Test
    public void isPresentShouldReturnFalseWhenItemIsNeitherInInventoryOrInRoom() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = Item.newPortableObjectItem("key", "A small key.");
        GameState gameState = new GameState(cell);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.NORTH);
        Condition isPresent = new Conditions.IS_PRESENT(key);
        assertFalse(isPresent.apply(playerCommand, gameState));
    }

    @Test
    public void notShouldReturnLogicalComplementOfGivenCondition() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = Item.newPortableObjectItem("key", "A small key.", cell);
        GameState gameState = new GameState(cell);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.NORTH);
        Condition isPresent = new Conditions.IS_PRESENT(key);
        Condition isNotPresent = new Conditions.NOT(isPresent);
        assertEquals(!isPresent.apply(playerCommand, gameState), isNotPresent.apply(playerCommand, gameState));
    }

    @Test
    public void occursRandomlyShouldReturnTrueGiven100PercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.OCCURS, Noun.UNRECOGNIZED);
        Condition occurs = new Conditions.OCCURS_RANDOMLY(100);
        assertTrue(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(100, () -> 0);
        assertTrue(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(100, () -> 50);
        assertTrue(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(100, () -> 100);
        assertTrue(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(100, () -> -1);
        assertTrue(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(100, () -> 101);
        assertTrue(occurs.apply(playerCommand, gameState));
    }

    @Test
    public void occursRandomlyShouldReturnFalseGivenZeroPercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.OCCURS, Noun.UNRECOGNIZED);
        Condition occurs = new Conditions.OCCURS_RANDOMLY(0);
        assertFalse(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(0, () -> 0);
        assertFalse(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(0, () -> 50);
        assertFalse(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(0, () -> 100);
        assertFalse(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(0, () -> -1);
        assertFalse(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(0, () -> 101);
        assertFalse(occurs.apply(playerCommand, gameState));
    }

    @Test
    public void occursRandomlyShouldReturnTrueGivenSuppliedNumberIsLessThenProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.OCCURS, Noun.UNRECOGNIZED);
        Condition occurs = new Conditions.OCCURS_RANDOMLY(25, () -> 24);
        assertTrue(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(25, () -> 0);
        assertTrue(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(25, () -> 50);
        assertFalse(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(25, () -> 100);
        assertFalse(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(25, () -> -1);
        assertTrue(occurs.apply(playerCommand, gameState));
        occurs = new Conditions.OCCURS_RANDOMLY(25, () -> 101);
        assertFalse(occurs.apply(playerCommand, gameState));
    }

    @Test
    public void itemMovedShouldReturnTrueIfItemsCurrentLocationDoesNotMatchItsStartingLocation() {
        Item item = Item.newPortableObjectItem("chalice", "A jewel-encrusted golden chalice.");
        PlayerCommand playerCommand = new PlayerCommand(new Verb("PICKUP"), new Noun("chalice"));
        GameState gameState = new GameState(Room.NOWHERE);
        Condition itemMoved = new Conditions.ITEM_MOVED(item);
        assertFalse(itemMoved.apply(playerCommand, gameState));
        item.stow();
        assertTrue(itemMoved.apply(playerCommand, gameState));
        item.drop(Room.NOWHERE);
        assertFalse(itemMoved.apply(playerCommand, gameState));
    }
}
