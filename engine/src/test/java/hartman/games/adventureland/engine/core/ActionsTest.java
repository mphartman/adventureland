package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
                .on(new Verb("beat"))
                .then((command, gameState, display) -> gameState.setFlag("verb", command.getVerb()))
                .build();
        GameState gameState = new GameState(Room.NOWHERE);
        action.run(gameState, message -> {}, new Command(new Verb("beat"), Noun.NONE));
        assertTrue(new Verb("beat").equals(gameState.getFlag("verb")));
    }

    @Test
    public void builderReturnsActionWithNounMatchingConditionGivenNoun() {
        Action action = Actions.newActionSet().newAction()
                .with(new Noun("fork"))
                .then((command, gameState, display) -> gameState.setFlag("noun", command.getNoun()))
                .build();
        GameState gameState = new GameState(Room.NOWHERE);
        action.run(gameState, message -> {}, new Command(Verb.NONE, new Noun("fork")));
        assertTrue(new Noun("fork").equals(gameState.getFlag("noun")));
    }

    @Test
    public void builderReturnsActionWithConditionCapableOfMatchingAnyNoun() {
        Action action = Actions.newActionSet().newAction().onNoVerb().withAnyOf(Nouns.NORTH, Nouns.SOUTH, Nouns.EAST, Nouns.WEST).build();
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("n"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("s"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("e"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("w"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("north"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("south"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("east"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("west"))));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, Nouns.NORTH)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, Nouns.SOUTH)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, Nouns.EAST)));
        assertTrue(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, Nouns.WEST)));
        assertFalse(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("u"))));
        assertFalse(action.run(new GameState(Room.NOWHERE), m -> {}, new Command(Verb.NONE, new Noun("d"))));
    }

    @Test
    public void buildVocabularyShouldContainVerbsAndNouns() {
        Actions actionSet = Actions.newActionSet();
        actionSet.newAction().onNoVerb().build();
        actionSet.newAction().onAnyVerb().build();
        actionSet.newAction().onUnrecognizedVerb();
        actionSet.newAction().on(new Verb("type"));
        actionSet.newAction().on(new Verb("type"));
        actionSet.newAction().on(new Verb("drive"));
        actionSet.newAction().withNoNoun().build();
        actionSet.newAction().withAnyNoun().build();
        actionSet.newAction().withUnrecognizedNoun().build();
        actionSet.newAction().with(new Noun("keyboard")).build();
        actionSet.newAction().with(new Noun("keyboard")).build();
        actionSet.newAction().with(new Noun("chair")).build();

        Vocabulary vocab = actionSet.buildVocabulary();
        assertTrue(vocab.findMatch(new Verb("type")).isPresent());
        assertTrue(vocab.findMatch(new Verb("drive")).isPresent());
        assertTrue("Any always matches", vocab.findMatch(Verb.ANY).isPresent());
        assertFalse(vocab.findMatch(Verb.NONE).isPresent());
        assertFalse(vocab.findMatch(Verb.UNRECOGNIZED).isPresent());
        assertTrue(vocab.findMatch(new Noun("keyboard")).isPresent());
        assertTrue(vocab.findMatch(new Noun("chair")).isPresent());
        assertTrue("Any always matches", vocab.findMatch(Noun.ANY).isPresent());
        assertFalse(vocab.findMatch(Noun.NONE).isPresent());
        assertFalse(vocab.findMatch(Noun.UNRECOGNIZED).isPresent());
    }

    @Test
    public void addAllMergesActions() {
        Actions actionSet1 = Actions.newActionSet();
        Action a1 = actionSet1.newAction().build();

        Actions actionSet2 = Actions.newActionSet();
        Action a2 = actionSet2.newAction().build();

        Actions actionSet3 = actionSet1.addAll(actionSet2);

        assertTrue(actionSet1.copyOfActions().contains(a1));
        assertTrue(actionSet1.copyOfActions().contains(a2));
        assertTrue(actionSet2.copyOfActions().contains(a2));
        assertFalse(actionSet2.copyOfActions().contains(a1));
        assertTrue(actionSet3.copyOfActions().contains(a1));
        assertTrue(actionSet3.copyOfActions().contains(a2));
    }
}
