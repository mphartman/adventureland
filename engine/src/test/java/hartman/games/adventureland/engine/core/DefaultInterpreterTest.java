package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.PlayerCommand;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class DefaultInterpreterTest {

    @Test
    public void nextCommandShouldReturnPlayerCommandGivenValidVerbAndNoun() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(Arrays.asList(new Verb("JUMP"))), new HashSet<>(Arrays.asList(new Noun("UP"))));
        DefaultInterpreter interpreter = new DefaultInterpreter(new Scanner("JUMP UP"), vocabulary);
        PlayerCommand playerCommand = interpreter.nextCommand();
        assertTrue(playerCommand.getVerb().equals(new Verb("JUMP")));
        assertTrue(playerCommand.getNoun().equals(new Noun("UP")));
    }

    @Test
    public void nextCommandShouldReturnUnrecognizedGivenVerbOrNounNotInVocabulary() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(Arrays.asList(new Verb("JUMP"))), new HashSet<>(Arrays.asList(new Noun("UP"))));
        DefaultInterpreter interpreter = new DefaultInterpreter(new Scanner("KILL TROLL"), vocabulary);
        PlayerCommand playerCommand = interpreter.nextCommand();
        assertTrue(playerCommand.getVerb().equals(Verb.UNRECOGNIZED));
        assertTrue(playerCommand.getNoun().equals(Noun.UNRECOGNIZED));
    }
}