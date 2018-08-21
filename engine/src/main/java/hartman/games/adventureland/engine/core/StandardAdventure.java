package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;

import java.util.LinkedHashSet;
import java.util.Set;

import static hartman.games.adventureland.engine.core.Actions.newActionSet;
import static hartman.games.adventureland.engine.core.Conditions.not;
import static hartman.games.adventureland.engine.core.Conditions.roomHasExit;
import static hartman.games.adventureland.engine.core.Conditions.times;
import static hartman.games.adventureland.engine.core.Results.go;
import static hartman.games.adventureland.engine.core.Results.inventory;
import static hartman.games.adventureland.engine.core.Results.look;
import static hartman.games.adventureland.engine.core.Results.println;
import static hartman.games.adventureland.engine.core.Results.quit;
import static hartman.games.adventureland.engine.core.Words.DOWN;
import static hartman.games.adventureland.engine.core.Words.EAST;
import static hartman.games.adventureland.engine.core.Words.GO;
import static hartman.games.adventureland.engine.core.Words.HELP;
import static hartman.games.adventureland.engine.core.Words.INVENTORY;
import static hartman.games.adventureland.engine.core.Words.LOOK;
import static hartman.games.adventureland.engine.core.Words.NORTH;
import static hartman.games.adventureland.engine.core.Words.QUIT;
import static hartman.games.adventureland.engine.core.Words.SOUTH;
import static hartman.games.adventureland.engine.core.Words.UP;
import static hartman.games.adventureland.engine.core.Words.WEST;
import static java.util.Arrays.asList;

public final class StandardAdventure {

    private StandardAdventure() {
        throw new IllegalStateException("utility class");
    }

    private static final Set<Action> standardOccurs;
    private static final Set<Action> standardActions;
    private static final Vocabulary standardVocabulary;

    static {
        Actions occurs = newActionSet();

        // `look` on game startup
        occurs.newAction()
                .when(times(1))
                .then(look)
                .build();

        standardOccurs = occurs.copyOfActions();
    }

    static {
        Actions actionSet = Actions.newActionSet();

        actionSet.newAction()
                .on(GO).withNoSecondWord()
                .then(println("Go where?"))
                .build();

        actionSet.newAction()
                .on(GO).withAnySecondWord()
                .when(roomHasExit)
                .then(go).andThen(look)
                .build();

        actionSet.newAction()
                .on(GO)
                .then(println("I can't go that way."))
                .build();

        actionSet.newAction()
                .onAnyFirstWords(asList(NORTH, SOUTH, UP, DOWN, EAST, WEST).toArray(new Word[0])).withNoSecondWord()
                .when(not(roomHasExit))
                .then(println("I can't go {verb} from here."))
                .build();

        actionSet.newAction()
                .onAnyFirstWord()
                .when(roomHasExit)
                .then(go).andThen(look)
                .build();

        actionSet.newAction()
                .on(LOOK)
                .then(look)
                .build();

        actionSet.newAction()
                .on(LOOK).withAnySecondWord()
                .then(look)
                .build();

        actionSet.newAction()
                .on(INVENTORY)
                .then(inventory)
                .build();

        actionSet.newAction()
                .on(INVENTORY).withAnySecondWord()
                .then(inventory)
                .build();

        actionSet.newAction()
                .on(QUIT)
                .then(quit)
                .build();

        actionSet.newAction()
                .on(HELP)
                .then(println("A voice BOOOMS out:\nTry --> \"GO, LOOK, JUMP, SWIM, CLIMB, TAKE, DROP\"\nand any other verbs you can think of..."))
                .build();

        // handle unrecognized input
        actionSet.newAction().onUnrecognizedFirstWord().then(println("Sorry, I don't know how to do that.")).build();
        actionSet.newAction().onUnrecognizedFirstWord().withUnrecognizedSecondWord().then(println("Sorry, I don't know how to do that with that thing.")).build();
        actionSet.newAction().onUnrecognizedFirstWord().withAnySecondWord().then(println("Sorry, I don't know how to that with a {noun}.")).build();
        actionSet.newAction().onAnyFirstWord().withUnrecognizedSecondWord().then(println("I don't know how to {verb} with that thing.")).build();
        actionSet.newAction().onAnyFirstWord().withNoSecondWord().then(println("{verb} what?")).build();
        actionSet.newAction().onAnyFirstWord().withAnySecondWord().then(println("I can't do that here right now.")).build();

        standardActions = actionSet.copyOfActions();
        standardVocabulary = actionSet.buildVocabulary();
    }

    public static Vocabulary standardVocabulary() {
        return standardVocabulary;
    }

    public static Set<Action> standardOccurs() {
        return standardOccurs;
    }

    public static Set<Action> standardActions() {
        return standardActions;
    }

    public static Adventure mergeWithStandardAdventure(Adventure adventure) {
        Set<Action> mergedActions = new LinkedHashSet<>(adventure.getActions());
        mergedActions.addAll(standardActions());
        Set<Action> mergedOccurs = new LinkedHashSet<>(adventure.getOccurs());
        mergedOccurs.addAll(standardOccurs());
        Vocabulary vocabulary = adventure.getVocabulary().merge(standardVocabulary());
        return new Adventure(vocabulary, mergedOccurs, mergedActions, adventure.getItems(), adventure.getStartRoom());
    }
}
