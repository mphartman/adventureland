package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Conditions;
import hartman.games.adventureland.engine.core.Results;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Nouns.EAST;
import static hartman.games.adventureland.engine.core.Nouns.NORTH;
import static hartman.games.adventureland.engine.core.Nouns.SOUTH;
import static hartman.games.adventureland.engine.core.Nouns.UP;
import static hartman.games.adventureland.engine.core.Nouns.WEST;
import static hartman.games.adventureland.engine.core.Verbs.GO;
import static hartman.games.adventureland.engine.core.Verbs.QUIT;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class GameTest {

    private static Set<Verb> setOf(Verb... verbs) {
        return new LinkedHashSet<>(Arrays.asList(verbs));
    }

    @Test
    public void gameShouldInvokeActionsGivenPlayerCommandsWhichChangeGameState() {

        Vocabulary vocabulary = new Vocabulary(setOf(GO, QUIT), new LinkedHashSet<>(Arrays.asList(NORTH, SOUTH, UP, DOWN, EAST, WEST)));

        Room dungeon = new Room("dungeon", "A miserable, dark place with cold stone floors and cracked walls.");
        Room chamber = new Room("chamber", "A clean, bright chamber with red carpet and floral drapes.");
        chamber.setExit(DOWN, dungeon);
        dungeon.setExit(UP, chamber);

        Action goAction = new Action.Builder().on(GO).withAnyNoun().when(Conditions.currentRoomHasExit).then(Results.go).build();
        Action quitAction = new Action.Builder().on(QUIT).then(Results.quit).build();
        Set<Action> actions = new LinkedHashSet<>(asList(quitAction, goAction));

        Adventure adventure = new Adventure(vocabulary, Collections.emptySet(), actions, Collections.emptySet(), chamber);

        Command[] commands = {
                new Command(GO, DOWN),
                new Command(GO, UP),
                new Command(GO, DOWN),
                new Command(QUIT)
        };
        AtomicInteger i = new AtomicInteger(0);
        CommandInterpreter interpreter = () -> commands[i.getAndIncrement()];

        GameState gameState = new GameState(adventure.getStartRoom());

        Game game = new Game(adventure, interpreter, gameState, msg -> {});
        game.run();

        assertEquals(dungeon, gameState.getCurrentRoom());
    }

    @Test
    public void gameShouldStopRunActionsAfterFirstOneWhichReturnsTrue() {

        Vocabulary vocabulary = new Vocabulary(setOf(GO, QUIT), new LinkedHashSet<>(Arrays.asList(NORTH, SOUTH, UP, DOWN, EAST, WEST)));

        Action.Result noOpResult = (command, gameState, display) -> {};

        Action action1 = new Action.Builder().on(GO).withAnyNoun().when((command, gameState) -> { gameState.setFlag("action1", "called"); return false; }).then(noOpResult).build();
        Action action2 = new Action.Builder().on(GO).withAnyNoun().when((command, gameState) -> { gameState.setFlag("action2", "called"); return true;  }).then(noOpResult).build();
        Action action3 = new Action.Builder().on(GO).withAnyNoun().when((command, gameState) -> { gameState.setFlag("action3", "called"); return true;  }).then(noOpResult).build();
        Action quitAction = new Action.Builder().on(QUIT).then(Results.quit).build();
        Set<Action> actions = new LinkedHashSet<>(asList(action1, action2, action3, quitAction));

        Adventure adventure = new Adventure(vocabulary, Collections.emptySet(), actions, Collections.emptySet(), Room.NOWHERE);

        Command[] commands = {
                new Command(GO, DOWN),
                new Command(QUIT)
        };
        AtomicInteger i = new AtomicInteger(0);
        CommandInterpreter interpreter = () -> commands[i.getAndIncrement()];

        GameState gameState = new GameState(adventure.getStartRoom());

        Game game = new Game(adventure, interpreter, gameState, msg -> {});
        game.run();

        assertEquals("called", gameState.getFlag("action1"));
        assertEquals("called", gameState.getFlag("action2"));
        assertNull(gameState.getFlag("action3"));
        assertFalse(gameState.isRunning());
    }
}
