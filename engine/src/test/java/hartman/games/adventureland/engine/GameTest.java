package hartman.games.adventureland.engine;

import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class GameTest {

    @Test
    public void gameShouldInvokeAllOccursBeforeActions() {

        AtomicInteger sequence = new AtomicInteger();

        Action occurs1 = new Action.Builder()
                .when((command, gameState) -> {
                    gameState.setFlag("occurs1", sequence.incrementAndGet());
                    return true;
                })
                .build();

        Action occurs2 = new Action.Builder()
                .when((command, gameState) -> {
                    gameState.setFlag("occurs2", sequence.incrementAndGet());
                    return false;
                })
                .build();

        Action occurs3 = new Action.Builder()
                .when((command, gameState) -> {
                    gameState.setFlag("occurs3", sequence.incrementAndGet());
                    return true;
                })
                .build();

        Action quit = new Action.Builder()
                .when((command, gameState) -> {
                    gameState.setFlag("quit", sequence.incrementAndGet());
                    return true;
                })
                .then((command, gameState, display) -> gameState.quit())
                .build();

        Vocabulary vocabulary = new Vocabulary(emptySet());
        Adventure adventure = new Adventure(vocabulary, new LinkedHashSet<>(asList(occurs1, occurs2, occurs3)), singleton(quit), emptySet(), Room.NOWHERE);


        Game game = new Game(adventure, () -> Command.NONE, message -> {});
        GameState gameState = game.run();

        assertFalse(gameState.isRunning());
        assertEquals(1, gameState.getFlag("occurs1"));
        assertEquals(2, gameState.getFlag("occurs2"));
        assertEquals(3, gameState.getFlag("occurs3"));
        assertEquals(4, gameState.getFlag("quit"));
    }

    @Test
    public void runShouldReturnWhenGameStateIsNotRunning() {

        Action occurs = new Action.Builder()
                .then((command, gameState, display) -> gameState.quit())
                .build();

        Action action = new Action.Builder()
                .then((command, gameState, display) -> gameState.setFlag("action", "Should not have been called."))
                .build();

        Vocabulary vocabulary = new Vocabulary(emptySet());
        Adventure adventure = new Adventure(vocabulary, singleton(occurs), singleton(action), emptySet(), Room.NOWHERE);

        Game game = new Game(adventure, () -> Command.NONE, message -> {});
        GameState gameState = game.run();

        assertFalse(gameState.isRunning());
        assertNull(gameState.getFlag("action"));
    }


}
