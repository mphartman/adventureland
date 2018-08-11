package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action.Condition;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Verb;
import org.junit.Test;

import java.util.stream.IntStream;

import static hartman.games.adventureland.engine.core.Conditions.carrying;
import static hartman.games.adventureland.engine.core.Conditions.exists;
import static hartman.games.adventureland.engine.core.Conditions.hasMoved;
import static hartman.games.adventureland.engine.core.Conditions.here;
import static hartman.games.adventureland.engine.core.Conditions.in;
import static hartman.games.adventureland.engine.core.Conditions.not;
import static hartman.games.adventureland.engine.core.Conditions.present;
import static hartman.games.adventureland.engine.core.Conditions.random;
import static hartman.games.adventureland.engine.core.Conditions.roomHasExit;
import static hartman.games.adventureland.engine.core.Conditions.there;
import static hartman.games.adventureland.engine.core.Conditions.times;
import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionsTest {

    @Test
    public void hasExitShouldReturnFalseWhenRoomHasNoExits() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, DOWN);
        assertFalse(roomHasExit.matches(command, gameState));
        command = new Command(Verbs.GO, Nouns.UP);
        assertFalse(roomHasExit.matches(command, gameState));
    }

    @Test
    public void hasExitShouldReturnTrueWhenRoomHasRequestedExit() {
        Room end = new Room("end", "end");
        Room start = new Room("start", "start");
        start.setExit(DOWN, end);
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, DOWN);
        assertTrue(roomHasExit.matches(command, gameState));
        command = new Command(Verbs.GO, Nouns.UP);
        assertFalse(roomHasExit.matches(command, gameState));
    }

    @Test
    public void inRoomShouldReturnFalseWhenPlayerIsNotInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition inRoom = in(Room.NOWHERE);
        assertFalse(inRoom.matches(command, gameState));
    }

    @Test
    public void inRoomShouldReturnTrueWhenPlayerIsInRoom() {
        Room start = new Room("start", "start");
        GameState gameState = new GameState(start);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition inRoom = in(start);
        assertTrue(inRoom.matches(command, gameState));
    }

    @Test
    public void itemCarriedShouldReturnFalseWhenPlayerInventoryDoesNotHaveItem() {
        Item dagger = new Item.Builder().named("dagger").describedAs("A dull, chipped blade.").portable().in(Room.NOWHERE).build();
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemCarried = carrying(dagger);
        assertFalse(itemCarried.matches(command, gameState));
    }

    @Test
    public void itemCarriedShouldReturnTrueWhenPlayerInventoryHasItem() {
        Item torch = new Item.Builder().named("torch").describedAs("An unlit wooden torch dipped in pitch.").inInventory().build();
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemCarried = carrying(torch);
        assertTrue(itemCarried.matches(command, gameState));
    }

    @Test
    public void itemHereShouldReturnTrueWhenItemIsInRoom() {
        Room entryway = new Room("entryway", "A dark, narrow entry way into the house.");
        Item dog = new Item.Builder().named("dog").describedAs("A large, rapid dog growls at me.").in(entryway).build();
        GameState gameState = new GameState(entryway);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemHere = here(dog);
        assertTrue(itemHere.matches(command, gameState));
    }

    @Test
    public void itemHereShouldReturnFalseWhenItemIsNotInRoom() {
        Room bathroom = new Room("bathroom", "A luxurious master bathroom with a claw-foot tub.");
        Item microwave = new Item.Builder().named("microwave").describedAs("A 1200-watt microwave.").build();
        GameState gameState = new GameState(bathroom);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition itemHere = here(microwave);
        assertFalse(itemHere.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInRoom() {
        Room doghouse = new Room("doghouse", "A cozy, warm kennel.");
        Item dog = new Item.Builder().named("dog").describedAs("A small sleeps here.").in(doghouse).build();
        GameState gameState = new GameState(doghouse);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition isPresent = present(dog);
        assertTrue(isPresent.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnTrueWhenItemIsInInventory() {
        Item key = new Item.Builder().named("key").describedAs("A tarnished brass skeleton key.").inInventory().build();
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verbs.GO, Noun.ANY);
        Condition isPresent = present(key);
        assertTrue(isPresent.matches(command, gameState));
    }

    @Test
    public void isPresentShouldReturnFalseWhenItemIsNeitherInInventoryOrInRoom() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = new Item.Builder().named("key").describedAs("A small key").portable().build();
        GameState gameState = new GameState(cell);
        Command command = new Command(Verbs.GO, Nouns.NORTH);
        Condition isPresent = present(key);
        assertFalse(isPresent.matches(command, gameState));
    }

    @Test
    public void notShouldReturnLogicalComplementOfGivenCondition() {
        Room cell = new Room("cell", "A filthy, tiny prison cell.");
        Item key = new Item.Builder().named("key").describedAs("A small key.").in(cell).build();
        GameState gameState = new GameState(cell);
        Command command = new Command(Verbs.GO, Nouns.NORTH);
        Condition isPresent = present(key);
        Condition isNotPresent = not(isPresent);
        assertEquals(!isPresent.matches(command, gameState), isNotPresent.matches(command, gameState));
    }

    @Test
    public void randomShouldReturnTrueGiven100PercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verb.ANY, Noun.UNRECOGNIZED);

        IntStream.range(0, 100).forEachOrdered(i -> assertTrue(random(100, () -> i).matches(command, gameState)));
    }

    @Test
    public void randomShouldReturnFalseGivenZeroPercentProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verb.ANY, Noun.UNRECOGNIZED);

        IntStream.range(0, 100).forEachOrdered(i -> assertFalse(random(0, () -> i).matches(command, gameState)));
    }

    @Test
    public void randomShouldReturnTrueGivenSuppliedNumberIsLessThenProbability() {
        GameState gameState = new GameState(Room.NOWHERE);
        Command command = new Command(Verb.ANY, Noun.UNRECOGNIZED);

        IntStream.range(0, 100).forEachOrdered(i ->
                assertTrue(random(i + 1, () -> i).matches(command, gameState)));
    }

    @Test
    public void itemMovedShouldReturnTrueIfItemsCurrentLocationDoesNotMatchItsStartingLocation() {
        Item item = new Item.Builder().named("chalice").describedAs("A jewel-encrusted golden chalice.").portable().build();
        Command command = new Command(new Verb("PICKUP"), new Noun("chalice"));
        GameState gameState = new GameState(Room.NOWHERE);
        assertFalse(hasMoved(item).matches(command, gameState));
        item.stow();
        assertTrue(hasMoved(item).matches(command, gameState));
        item.drop(Room.NOWHERE);
        assertFalse(hasMoved(item).matches(command, gameState));
    }

    @Test
    public void timesShouldReturnTrueForTheGivenNumberOfTimes() {
        GameState gameState = new GameState(Room.NOWHERE);

        Condition zero = times(0);
        assertFalse(zero.matches(Command.NONE, gameState));
        assertFalse(zero.matches(Command.NONE, gameState));

        Condition once = times(1);
        assertTrue(once.matches(Command.NONE, gameState));
        assertFalse(once.matches(Command.NONE, gameState));

        Condition never = times(-1);
        assertFalse(never.matches(Command.NONE, gameState));
        assertFalse(never.matches(Command.NONE, gameState));
    }

    @Test
    public void itemIsInRoomShouldReturnTrueIfItemIsInRoom() {
        Room bus = new Room("bus", "A city bus.");
        Item vomit = new Item.Builder().named("vomit").in(bus).build();
        Item driver = new Item.Builder().named("driver").build();

        assertTrue(there(vomit, bus).matches(Command.NONE, new GameState(bus)));
        assertFalse(there(driver, bus).matches(Command.NONE, new GameState(bus)));
    }

    @Test
    public void existsShouldReturnTrueForNonDestroyedGameItems() {
        Items.ItemSet itemSet = Items.newItemSet();
        Item pencil = itemSet.newItem().named("pencil").build();

        assertFalse(exists(pencil).matches(Command.NONE, new GameState(Room.NOWHERE)));

        GameState gameState = new GameState(Room.NOWHERE, itemSet.copyOfItems());
        assertTrue(exists(pencil).matches(Command.NONE, gameState));

        gameState.destroy(pencil);
        assertFalse(exists(pencil).matches(Command.NONE, gameState));
    }
}
