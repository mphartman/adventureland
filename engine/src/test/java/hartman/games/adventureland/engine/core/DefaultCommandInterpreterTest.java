package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class DefaultCommandInterpreterTest {

    @Test
    public void nextCommandShouldReturnPlayerCommandGivenValidVerbAndNoun() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(Arrays.asList(new Verb("JUMP"))), new HashSet<>(Arrays.asList(new Noun("UP"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("JUMP UP"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().equals(new Verb("JUMP")));
        assertTrue(command.getNoun().equals(new Noun("UP")));
    }

    @Test
    public void nextCommandShouldReturnUnrecognizedGivenVerbOrNounNotInVocabulary() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(Arrays.asList(new Verb("JUMP"))), new HashSet<>(Arrays.asList(new Noun("UP"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("KILL TROLL"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().equals(Verb.UNRECOGNIZED));
        assertTrue(command.getNoun().equals(Noun.UNRECOGNIZED));
    }
}