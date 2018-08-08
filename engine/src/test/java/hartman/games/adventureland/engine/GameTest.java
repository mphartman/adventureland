package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Conditions;
import hartman.games.adventureland.engine.core.Nouns;
import hartman.games.adventureland.engine.core.Results;
import hartman.games.adventureland.engine.core.Verbs;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static hartman.games.adventureland.engine.Action.setOf;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class GameTest {

    @Test
    public void gameShouldInvokeActionsGivenPlayerCommandsWhichChangeGameState() {

        Vocabulary vocabulary = new Vocabulary(Vocabulary.setOf(Verbs.GO), Nouns.directions());

        Room dungeon = new Room("dungeon", "A miserable, dark place with cold stone floors and cracked walls.");
        Room chamber = new Room("chamber", "A clean, bright chamber with red carpet and floral drapes.");
        chamber.setExit(Nouns.DOWN, dungeon);
        dungeon.setExit(Nouns.UP, chamber);

        Action goAction = new Action(Verbs.GO, Noun.ANY, setOf(Conditions.HAS_EXIT), setOf(Results.GOTO));
        Set<Action> actions = new HashSet<>(asList(Actions.QUIT_ACTION, goAction));

        Adventure adventure = new Adventure(vocabulary, Collections.emptySet(), actions, chamber);

        Command[] commands = {
                new Command(Verbs.GO, Nouns.DOWN),
                new Command(Verbs.GO, Nouns.UP),
                new Command(Verbs.GO, Nouns.DOWN),
                new Command(Verbs.QUIT)
        };
        AtomicInteger i = new AtomicInteger(0);
        CommandInterpreter interpreter = () -> commands[i.getAndIncrement()];

        GameState gameState = new GameState(chamber);

        Game game = new Game(adventure, interpreter, gameState, msg -> {});

        game.run();
        assertEquals(dungeon, gameState.getCurrentRoom());
    }


}
