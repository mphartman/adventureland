package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ActionTest {

    @Test
    public void runShouldExecuteResultsWhenConditionsAreAllTrue() {
        Action action = new Action.Builder()
                .when((command, gameState) -> new Word("SLAP").matches(command.getVerb()))
                .then((command, gameState, display) -> gameState.setFlag("MESSAGE", "Ouch! That hurts!"))
                .build();

        GameState gameState = new GameState(Room.NOWHERE);

        action.run(gameState, msg -> {}, new Command(new Word("SHOUT"), Word.NONE));
        assertNull(gameState.getFlag("MESSAGE"));

        action.run(gameState, msg -> {}, new Command(new Word("SLAP"), Word.NONE));
        assertEquals("Ouch! That hurts!", gameState.getFlag("MESSAGE"));
    }

}
