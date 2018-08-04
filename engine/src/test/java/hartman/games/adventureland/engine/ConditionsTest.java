package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.Action.Condition;
import hartman.games.adventureland.engine.core.Conditions;
import hartman.games.adventureland.engine.core.Nouns;
import hartman.games.adventureland.engine.core.Verbs;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConditionsTest {

    @Test
    public void hasExitShouldReturnFalseWhenRoomHasNoExits() {
        Room start = new Room("start", "start");
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.DOWN, gameState);
        assertFalse(Conditions.HAS_EXIT.apply(playerCommand));
        playerCommand = new PlayerCommand(Verbs.GO, Nouns.UP, gameState);
        assertFalse(Conditions.HAS_EXIT.apply(playerCommand));
    }

    @Test
    public void hasExitShouldReturnTrueWhenRoomHasRequestedExit() {
        Room end = new Room("end", "end");
        Room start = new Room("start", "start", new Room.Exit.Builder().exit(Direction.DOWN).towards(end).build());
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.DOWN, gameState);
        assertTrue(Conditions.HAS_EXIT.apply(playerCommand));
        playerCommand = new PlayerCommand(Verbs.GO, Nouns.UP, gameState);
        assertFalse(Conditions.HAS_EXIT.apply(playerCommand));
    }

    @Test
    public void inRoomShouldReturnFalseWhenPlayerIsNotInRoom() {
        Room start = new Room("start", "start");
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.ANY, gameState);
        Condition inRoom = new Conditions.IN_ROOM(Room.NOWHERE);
        assertFalse(inRoom.apply(playerCommand));
    }

    @Test
    public void inRoomShouldReturnTrueWhenPlayerIsInRoom() {
        Room start = new Room("start", "start");
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, start);
        PlayerCommand playerCommand = new PlayerCommand(Verbs.GO, Nouns.ANY, gameState);
        Condition inRoom = new Conditions.IN_ROOM(start);
        assertTrue(inRoom.apply(playerCommand));
    }
}
