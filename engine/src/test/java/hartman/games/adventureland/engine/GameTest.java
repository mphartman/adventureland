package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Nouns;
import hartman.games.adventureland.engine.core.Verbs;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static org.junit.Assert.assertEquals;

public class GameTest {

    @Test
    public void gameTickShouldInvokeActionsGivenPlayerCommandsWhichChangeGameState() {

        Room dungeon = new Room("dungeon", "A miserable, dark place with cold stone floors and cracked walls.");
        Room chamber = new Room("chamber", "A clean, bright chamber with red carpet and floral drapes.");
        chamber.setExit(Nouns.DOWN, dungeon);
        dungeon.setExit(Nouns.UP, chamber);
        Set<Room> rooms = new HashSet<>(asList(chamber, dungeon));

        Vocabulary vocabulary = new Vocabulary(Verbs.asSet(Verbs.GO), Nouns.directions());

        Set<Action> actions = new HashSet<>(asList(Actions.GO_ACTION));

        Adventure testAdventure = new Adventure(vocabulary, rooms, actions);

        SequencePlaybackInterpreter commandPlayback = new SequencePlaybackInterpreter(
                new PlayerCommand(Verbs.GO, Nouns.DOWN),
                new PlayerCommand(Verbs.GO, Nouns.UP)
        );

        GameState gameState = new GameState(chamber);

        Game game = new Game(testAdventure, commandPlayback, gameState);

        game.tick();
        assertEquals(dungeon, gameState.getCurrentRoom());

        game.tick();
        assertEquals(chamber, gameState.getCurrentRoom());
    }


}

/**
 * Holds a sequence of player commands. Each call to nextCommand returns next command in sequence.
 */
class SequencePlaybackInterpreter implements Interpreter {
    private final PlayerCommand[] commands;
    private int i;

    SequencePlaybackInterpreter(PlayerCommand... commands) {
        this.commands = copyOf(commands, commands.length);
    }

    @Override
    public void setVocabulary(Vocabulary vocabulary) {
        // ignored
    }

    @Override
    public PlayerCommand nextCommand() {
        return commands[i++];
    }
}