package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ActionsTest {

    @Test
    public void builderReturnsActionWithConditionAndResult() {
        Actions actionSet = Actions.newActionSet();
        Action action = actionSet.newAction()
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

    @Test
    public void builderReturnsActionWithVerbMatchingConditionGivenVerb() {
        Action action = Actions.newActionSet().newAction()
                .on(new Word("beat"))
                .then((command, gameState, display) -> gameState.setFlag("verb", command.getFirstWord()))
                .build();
        GameState gameState = new GameState(Room.NOWHERE);
        action.run(gameState, message -> {}, new Command(new Word("beat"), Word.NONE));
        assertTrue(new Word("beat").equals(gameState.getFlag("verb")));
    }

    @Test
    public void builderReturnsActionWithNounMatchingConditionGivenNoun() {
        Action action = Actions.newActionSet().newAction()
                .with(new Word("fork"))
                .then((command, gameState, display) -> gameState.setFlag("noun", command.getSecondWord()))
                .build();
        GameState gameState = new GameState(Room.NOWHERE);
        action.run(gameState, message -> {}, new Command(Word.NONE, new Word("fork")));
        assertTrue(new Word("fork").equals(gameState.getFlag("noun")));
    }

    @Test
    public void builderReturnsActionWithConditionCapableOfMatchingAnyNoun() {
        Action action = Actions.newActionSet().newAction()
                .onNoFirstWord()
                .withAnySecondWords(Words.NORTH, Words.SOUTH, Words.EAST, Words.WEST)
                .build();
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("n"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("s"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("e"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("w"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("north"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("south"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("east"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("west"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, Words.NORTH)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, Words.SOUTH)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, Words.EAST)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, Words.WEST)));
        assertFalse(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("u"))));
        assertFalse(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Word.NONE, new Word("d"))));
    }

    @Test
    public void builderReturnsActionWithConditionCapableOfMatchingAnyVerb() {
        Action action = Actions.newActionSet().newAction()
                .onAnyFirstWords(new Word("kill", "k"), new Word("slay", "s"), new Word("murder", "m"))
                .withNoSecondWord()
                .build();
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(new Word("kill"), Word.NONE)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(new Word("k"), Word.NONE)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(new Word("slay"), Word.NONE)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(new Word("s"), Word.NONE)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(new Word("murder"), Word.NONE)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(new Word("m"), Word.NONE)));
        assertFalse(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(new Word("hug"), Word.NONE)));
        assertFalse(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(new Word("kiss"), Word.NONE)));
    }

    @Test
    public void buildVocabularyShouldContainVerbsAndNouns() {
        Actions actionSet = Actions.newActionSet();
        actionSet.newAction().onNoFirstWord().build();
        actionSet.newAction().onAnyFirstWord().build();
        actionSet.newAction().onUnrecognizedFirstWord();
        actionSet.newAction().on(new Word("type"));
        actionSet.newAction().on(new Word("type"));
        actionSet.newAction().on(new Word("drive"));

        actionSet.newAction().withNoSecondWord().build();
        actionSet.newAction().withAnySecondWord().build();
        actionSet.newAction().withUnrecognizedSecondWord().build();
        actionSet.newAction().with(new Word("keyboard")).build();
        actionSet.newAction().with(new Word("keyboard")).build();
        actionSet.newAction().with(new Word("chair")).build();

        Vocabulary vocab = actionSet.buildVocabulary();

        assertTrue(vocab.findMatch(new Word("type")).isPresent());
        assertTrue(vocab.findMatch(new Word("drive")).isPresent());
        assertTrue("Any always matches", vocab.findMatch(Word.ANY).isPresent());
        assertFalse(vocab.findMatch(Word.NONE).isPresent());
        assertFalse(vocab.findMatch(Word.UNRECOGNIZED).isPresent());

        assertTrue(vocab.findMatch(new Word("keyboard")).isPresent());
        assertTrue(vocab.findMatch(new Word("chair")).isPresent());
        assertTrue("Any always matches", vocab.findMatch(Word.ANY).isPresent());
        assertFalse(vocab.findMatch(Word.NONE).isPresent());
        assertFalse(vocab.findMatch(Word.UNRECOGNIZED).isPresent());
    }

    @Test
    public void mergeCombinesWordsAndActions() {
        Actions actionSet1 = Actions.newActionSet();
        Action a1 = actionSet1.newAction().build();

        Actions actionSet2 = Actions.newActionSet();
        Action a2 = actionSet2.newAction().build();

        Actions actionSet3 = actionSet1.merge(actionSet2);

        assertTrue(actionSet1.copyOfActions().contains(a1));
        assertFalse(actionSet1.copyOfActions().contains(a2));
        assertTrue(actionSet2.copyOfActions().contains(a2));
        assertFalse(actionSet2.copyOfActions().contains(a1));
        assertTrue(actionSet3.copyOfActions().contains(a1));
        assertTrue(actionSet3.copyOfActions().contains(a2));
    }

    @Test
    public void testActionOnVerbWithNoNoun_CommandKnownVerbAndNoun() {

        Action action = Actions.newActionSet().newAction()
                .on(Words.GO)
                .withNoSecondWord()
                .then((command, gameState, display) -> gameState.setFlag("shazam", true))
                .build();

        GameState gameState = new GameState(Room.NOWHERE);
        action.run(gameState, m -> {}, new Command(new Word("go"), Word.NONE));
        assertEquals(true, gameState.getFlag("shazam"));

        gameState = new GameState(Room.NOWHERE);
        action.run(gameState, m -> {}, new Command(new Word("go"), new Word("dog")));
        assertNull(gameState.getFlag("shazam"));

    }

    @Test
    public void testActionOnVerbWithAnyNoun_CommandKnownVerbAndNoun() {

        Action action = Actions.newActionSet().newAction()
                .on(Words.GO)
                .withAnySecondWord()
                .then((command, gameState, display) -> gameState.setFlag("shazam", true))
                .build();

        GameState gameState = new GameState(Room.NOWHERE);
        action.run(gameState, m -> {}, new Command(new Word("go"), Word.NONE));
        assertNull(gameState.getFlag("shazam"));

        gameState = new GameState(Room.NOWHERE);
        action.run(gameState, m -> {}, new Command(new Word("go"), new Word("dog")));
        assertEquals(true, gameState.getFlag("shazam"));

        gameState = new GameState(Room.NOWHERE);
        action.run(gameState, m -> {}, new Command(new Word("go"), Word.UNRECOGNIZED));
        assertEquals(true, gameState.getFlag("shazam"));
    }
}
