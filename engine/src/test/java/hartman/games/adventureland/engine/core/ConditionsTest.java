package hartman.games.adventureland.engine.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import hartman.games.adventureland.engine.Action.Condition;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Player;
import hartman.games.adventureland.engine.PlayerCommand;
import hartman.games.adventureland.engine.Room;

public class ConditionsTest {

    @Test
    public void hasExitShouldReturnFalseWhenRoomHasNoExits() {
        Room start = new Room("start", "start");
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.DOWN);
        assertFalse(Conditions.HAS_EXIT.apply(playerCommand, gameState));
        playerCommand = new PlayerCommand(Verbs.GO, Nouns.UP);
        assertFalse(Conditions.HAS_EXIT.apply(playerCommand, gameState));
    }

    @Test
    public void hasExitShouldReturnTrueWhenRoomHasRequestedExit() {
        Room end = new Room("end", "end");
        Room start = new Room("start", "start", new Room.Exit.Builder().exit(Nouns.DOWN).towards(end).build());
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.DOWN);
        assertTrue(Conditions.HAS_EXIT.apply(playerCommand, gameState));
        playerCommand = new PlayerCommand(Verbs.GO, Nouns.UP);
        assertFalse(Conditions.HAS_EXIT.apply(playerCommand, gameState));
    }

    @Test
    public void inRoomShouldReturnFalseWhenPlayerIsNotInRoom() {
        Room start = new Room("start", "start");
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.ANY);
        Condition inRoom = new Conditions.IN_ROOM(Room.NOWHERE);
        assertFalse(inRoom.apply(playerCommand, gameState));
    }

    @Test
    public void inRoomShouldReturnTrueWhenPlayerIsInRoom() {
        Room start = new Room("start", "start");
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.ANY);
        Condition inRoom = new Conditions.IN_ROOM(start);
        assertTrue(inRoom.apply(playerCommand, gameState));
    }

    @Test
    public void itemCarriedShouldReturnFalseWhenPlayerInventoryDoesNotHaveItem() {
        Item dagger = new Item("dagger", "A dull, chipped blade.");
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.ANY);
        Condition itemCarried = new Conditions.ITEM_CARRIED(dagger);
        assertFalse(itemCarried.apply(playerCommand, gameState));
    }

    @Test
    public void itemCarriedShouldReturnTrueWhenPlayerInventoryHasItem() {
        Item torch = new Item("torch", "An unlit wooden torch dipped in pitch.");
        Player player = new Player("Archie");
        player.addToInventory(torch);
        GameState gameState = new GameState(player, Room.NOWHERE);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.ANY);
        Condition itemCarried = new Conditions.ITEM_CARRIED(torch);
        assertTrue(itemCarried.apply(playerCommand, gameState));
    }

    @Test
    public void itemHereShouldReturnTrueWhenItemIsInRoom() {
        Room entryway = new Room("entryway", "A dark, narrow entry way into the house.");
        Item dog = new Item("dog", "A large, rapid dog growls at me.", entryway);
        GameState gameState = new GameState(new Player("Archie"), entryway);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.ANY);
        Condition itemHere = new Conditions.ITEM_HERE(dog);
        assertTrue(itemHere.apply(playerCommand, gameState));
    }
}
