package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionTest {

    @Test
    public void runShouldExecuteResultsWhenConditionsAreAllTrue() {
        Action action = new Action.Builder()
                .when((command, gameState) -> new Word("SLAP").matches(command.getWordOrNone(1)))
                .then((command, gameState, display) -> gameState.setString("MESSAGE", "Ouch! That hurts!"))
                .build();

        GameState gameState = new GameState(Room.NOWHERE);

        action.run(gameState, new TestDisplay(), new Command(new Word("SHOUT"), Word.NONE));
        assertEquals("", gameState.getString("MESSAGE"));

        action.run(gameState, new TestDisplay(), new Command(new Word("SLAP"), Word.NONE));
        assertEquals("Ouch! That hurts!", gameState.getString("MESSAGE"));
    }

}
