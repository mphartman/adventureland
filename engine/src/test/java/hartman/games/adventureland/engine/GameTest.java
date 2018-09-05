package hartman.games.adventureland.engine;

import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GameTest {

    @Test
    public void gameShouldInvokeAllOccursAfterActions() {

        AtomicInteger sequence = new AtomicInteger();

        Action occurs1 = new Action.Builder()
                .when((command, gameState) -> {
                    gameState.setCounter("occurs1", sequence.incrementAndGet());
                    return true;
                })
                .build();

        Action occurs2 = new Action.Builder()
                .when((command, gameState) -> {
                    gameState.setCounter("occurs2", sequence.incrementAndGet());
                    return false;
                })
                .build();

        Action occurs3 = new Action.Builder()
                .when((command, gameState) -> {
                    gameState.setCounter("occurs3", sequence.incrementAndGet());
                    return true;
                })
                .build();

        Action quit = new Action.Builder()
                .when((command, gameState) -> {
                    gameState.setCounter("quit", sequence.incrementAndGet());
                    return true;
                })
                .then((command, gameState, display) -> gameState.quit())
                .build();

        Vocabulary vocabulary = new Vocabulary(emptySet());
        Adventure adventure = new Adventure(vocabulary, new LinkedHashSet<>(asList(occurs1, occurs2, occurs3)), singleton(quit), emptySet(), Room.NOWHERE);
        GameState gameState = new GameState(adventure.getStartRoom(), adventure.getItems());

        Game game = new Game(adventure, () -> Command.NONE, new TestDisplay(), gameState);
        gameState = game.run();

        assertFalse(gameState.isRunning());
        assertEquals(2, gameState.getCounter("occurs1"));
        assertEquals(3, gameState.getCounter("occurs2"));
        assertEquals(4, gameState.getCounter("occurs3"));
        assertEquals(1, gameState.getCounter("quit"));
    }

}
