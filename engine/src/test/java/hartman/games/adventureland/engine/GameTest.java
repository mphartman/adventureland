package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Conditions;
import hartman.games.adventureland.engine.core.Nouns;
import hartman.games.adventureland.engine.core.Results;
import hartman.games.adventureland.engine.core.Verbs;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static hartman.games.adventureland.engine.Action.setOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class GameTest {

    @Test
    public void gameShouldInvokeActionsGivenPlayerCommandsWhichChangeGameState() {

        Vocabulary vocabulary = new Vocabulary(Vocabulary.setOf(Verbs.GO, Verbs.QUIT), Nouns.directions());

        Room dungeon = new Room("dungeon", "A miserable, dark place with cold stone floors and cracked walls.");
        Room chamber = new Room("chamber", "A clean, bright chamber with red carpet and floral drapes.");
        chamber.setExit(Nouns.DOWN, dungeon);
        dungeon.setExit(Nouns.UP, chamber);

        Action goAction = new Action(Verbs.GO, Noun.ANY, setOf(Conditions.HasExit), setOf(Results.Go));
        Set<Action> actions = new LinkedHashSet<>(asList(Actions.QuitAction, goAction));

        Adventure adventure = new Adventure(vocabulary, Collections.emptySet(), actions, Collections.emptySet(), chamber);

        Command[] commands = {
                new Command(Verbs.GO, Nouns.DOWN),
                new Command(Verbs.GO, Nouns.UP),
                new Command(Verbs.GO, Nouns.DOWN),
                new Command(Verbs.QUIT)
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

        Vocabulary vocabulary = new Vocabulary(Vocabulary.setOf(Verbs.GO, Verbs.QUIT), Nouns.directions());

        Action.Result noOpResult = (command, gameState, display) -> {};
        Action action1 = new Action(Verbs.GO, Noun.ANY, setOf((command, gameState) -> { gameState.setFlag("action1", "called"); return false; } ), setOf(noOpResult));
        Action action2 = new Action(Verbs.GO, Noun.ANY, setOf((command, gameState) -> { gameState.setFlag("action2", "called"); return true; }), setOf(noOpResult));
        Action action3 = new Action(Verbs.GO, Noun.ANY, setOf((command, gameState) -> { gameState.setFlag("action3", "called"); return true; }), setOf(noOpResult));
        Set<Action> actions = new LinkedHashSet<>(asList(action1, action2, action3, Actions.QuitAction));

        Adventure adventure = new Adventure(vocabulary, Collections.emptySet(), actions, Collections.emptySet(), Room.NOWHERE);

        Command[] commands = {
                new Command(Verbs.GO, Nouns.DOWN),
                new Command(Verbs.QUIT)
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
