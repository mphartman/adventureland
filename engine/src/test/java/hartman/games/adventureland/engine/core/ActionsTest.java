package hartman.games.adventureland.engine.core;

import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Verbs.GO;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Player;
import hartman.games.adventureland.engine.PlayerCommand;
import hartman.games.adventureland.engine.Room;

public class ActionsTest {

    @Test
    public void goActionShouldMovePlayerToRoom() {
        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");
        Room.Exit downExit = new Room.Exit.Builder().exit(DOWN).towards(dungeon).build();
        Room chamber = new Room("chamber", "A small, round chamber with stone walls and floor.", downExit);
        Player player = new Player("Archie");
        GameState gameState = new GameState(player, chamber);
        PlayerCommand playerCommand = new PlayerCommand(GO, DOWN);
        
        Actions.GO_ACTION.run(playerCommand, gameState);

        assertEquals(dungeon, gameState.getPlayerCurrentPosition());
    }

}
