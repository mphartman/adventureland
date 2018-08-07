package hartman.games.adventureland.engine.core;

import org.junit.Test;

import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Room;

import static org.junit.Assert.*;

public class ResultsTest {
    
    @Test
    public void gotoRoomShouldMovePlayerInDirectionOfGivenNoun() {
        Room tower_second_floor = new Room("tower_second_floor", "Second story room of the tower.");
        Room tower_first_floor = new Room("tower_first_floor", "The first floor of a tall stone tower.", new Room.Exit.Builder().exit(Nouns.UP).towards(tower_second_floor).build());
        GameState gameState = new GameState(tower_first_floor);
        Command command = new Command(Verbs.GO, Nouns.UP);

        Results.GOTO.execute(command, gameState, msg -> {});

        assertEquals(tower_second_floor, gameState.getCurrentRoom());
    }

    @Test(expected = IllegalStateException.class)
    public void gotoRoomShouldThrowExceptionIfDirectionIsNotValidExitFromCurrentRoom() {
        Room sealed_tomb = new Room("sealed_tomb", "There is no escape.");
        GameState gameState = new GameState(sealed_tomb);
        Command command = new Command(Verbs.GO, Nouns.UP);

        Results.GOTO.execute(command, gameState, msg -> {});
    }

    @Test
    public void quitShouldChangeGameStateRunning() {
        GameState gameState = new GameState(Room.NOWHERE);
        assertTrue(gameState.isRunning());
        Results.QUIT.execute(Command.NONE, gameState, msg -> {});
        assertFalse(gameState.isRunning());
    }

}