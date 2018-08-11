package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Room;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActionsTest {

    @Test
    public void actionBuilderShouldCreateActionGivenConditionAndResult() {
        Actions.ActionSet actionSet = Actions.newActionSet();
        Action action = actionSet
                .newAction()
                .when((command, gameState) -> {
                    gameState.setFlag("condition", true);
                    return true;
                })
                .then((command, gameState, display) -> gameState.setFlag("result", true))
                .build();

        assertTrue(actionSet.copyOfActions().contains(action));

        GameState gameState = new GameState(Room.NOWHERE);
        action.run(gameState, message -> {}, Command.NONE);

        assertEquals(true, gameState.getFlag("condition"));
        assertEquals(true, gameState.getFlag("result"));
    }
}
