package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Condition;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Verb;
import org.junit.Test;

import static hartman.games.adventureland.engine.core.Conditions.HasItemMoved;
import static hartman.games.adventureland.engine.core.Conditions.IsInRoom;
import static hartman.games.adventureland.engine.core.Conditions.IsItemCarried;
import static hartman.games.adventureland.engine.core.Conditions.IsItemHere;
import static hartman.games.adventureland.engine.core.Conditions.IsPresent;
import static hartman.games.adventureland.engine.core.Conditions.Not;
import static hartman.games.adventureland.engine.core.Conditions.Random;
import static hartman.games.adventureland.engine.core.Conditions.currentRoomHasExit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionsTest {

    @Test
    public void hasExitShouldReturnFalseWhenRoomHasNoExits() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Nouns.DOWN);
        assertFalse(currentRoomHasExit.matches(command, gameState));
        command = new Command(Verbs.GO, Nouns.UP);
        assertFalse(currentRoomHasExit.matches(command, gameState));
    }

    @Test
    public void hasExitShouldReturnTrueWhenRoomHasRequestedExit() {
        Room end = new Room("end", "end");
        Room start = new Room("start", "start", new Room.Exit.Builder().exit(Nouns.DOWN).towards(end).build());
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Nouns.DOWN);
        assertTrue(currentRoomHasExit.matches(command, gameState));
        command = new Command(Verbs.GO, Nouns.UP);
        assertFalse(currentRoomHasExit.matches(command, gameState));
    }

    @Test
    public void inRoomShouldReturnFalseWhenPlayerIsNotInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition inRoom = new IsInRoom(Room.NOWHERE);
        assertFalse(inRoom.matches(command, gameState));
    }

    @Test
    public void inRoomShouldReturnTrueWhenPlayerIsInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition inRoom = new IsInRoom(start);
        assertTrue(inRoom.matches(command, gameState));
    }

    @Test
    public void itemCarriedShouldReturnFalseWhenPlayerInventoryDoesNotHaveItem() {
        Item dagger = new Item.Builder().named("dagger").describedAs("A dull, chipped blade.").portable().in(Room.NOWHERE).build();
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemCarried = new IsItemCarried(dagger);
        assertFalse(itemCarried.matches(command, gameState));
    }

    @Test
    public void itemCarriedShouldReturnTrueWhenPlayerInventoryHasItem() {
        Item torch = new Item.Builder().named("torch").describedAs("An unlit wooden torch dipped in pitch.").inInventory().build();
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemCarried = new IsItemCarried(torch);
        assertTrue(itemCarried.matches(command, gameState));
    }

    @Test
    public void itemHereShouldReturnTrueWhenItemIsInRoom() {
        Room entryway = new Room("entryway", "A dark, narrow entry way into the house.");
        Item dog = new Item.Builder().named("dog").describedAs("A large, rapid dog growls at me.").in(entryway).build();
        GameState gameState = new GameState(entryway);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemHere = new IsItemHere(dog);
        assertTrue(itemHere.matches(command, gameState));
    }

    @Test
    public void itemHereShouldReturnFalseWhenItemIsNotInRoom() {
        Room bathroom = new Room("bathroom", "A luxurious master bathroom with a claw-foot tub.");
        Item microwave = new Item.Builder().named("microwave").describedAs("A 1200-watt microwave.").build();
        GameState gameState = new GameState(bathroom);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemHere = new IsItemHere(microwave);
        assertFalse(itemHere.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInRoom() {
        Room doghouse = new Room("doghouse", "A cozy, warm kennel.");
        Item dog = new Item.Builder().named("dog").describedAs("A small sleeps here.").in(doghouse).build();
        GameState gameState = new GameState(doghouse);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition isPresent = new IsPresent(dog);
        assertTrue(isPresent.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInInventory() {
        Item key = new Item.Builder().named("key").describedAs("A tarnished brass skeleton key.").inInventory().build();
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition isPresent = new IsPresent(key);
        assertTrue(isPresent.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnFalseWhenItemIsNeitherInInventoryOrInRoom() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = new Item.Builder().named("key").describedAs("A small key").portable().build();
        GameState gameState = new GameState(cell);
        Command command = new Command(Verbs.GO, Nouns.NORTH);
        Condition isPresent = new IsPresent(key);
        assertFalse(isPresent.matches(command, gameState));
    }

    @Test
    public void notShouldReturnLogicalComplementOfGivenCondition() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = new Item.Builder().named("key").describedAs("A small key.").in(cell).build();
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
        Item item = new Item.Builder().named("chalice").describedAs("A jewel-encrusted golden chalice.").portable().build();
        Command command = new Command(new Verb("PICKUP"), new Noun("chalice"));
        GameState gameState = new GameState(Room.NOWHERE);
        Condition itemMoved = new HasItemMoved(item);
        assertFalse(itemMoved.matches(command, gameState));
        item.stow();
        assertTrue(itemMoved.matches(command, gameState));
        item.drop(Room.NOWHERE);
        assertFalse(itemMoved.matches(command, gameState));
    }
}
