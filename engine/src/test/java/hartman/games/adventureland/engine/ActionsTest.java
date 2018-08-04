package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Conditions;
import hartman.games.adventureland.engine.core.Nouns;
import hartman.games.adventureland.engine.core.Results;
import org.junit.Test;

import static hartman.games.adventureland.engine.Action.Condition;
import static hartman.games.adventureland.engine.Action.Result;
import static hartman.games.adventureland.engine.Vocabulary.Noun;
import static hartman.games.adventureland.engine.Vocabulary.Verb;
import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Verbs.GO;
import static org.junit.Assert.assertEquals;

public class ActionsTest {

    @Test
    public void goActionShouldMovePlayerToRoom() {
        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");

        Room.Exit downExit = new Room.Exit.Builder().exit(Direction.DOWN).towards(dungeon).build();

        Room chamber = new Room("chamber", "A small, round chamber with stone walls and floor.", downExit);

        Player player = new Player("Archie");
        GameState gameState = new GameState(player, chamber);

        PlayerCommand playerCommand = new PlayerCommand(GO, DOWN, gameState);

        Actions.GO_ACTION.run(playerCommand);

        assertEquals(dungeon, gameState.getPlayerCurrentPosition());
    }

}
