package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;
import org.junit.Test;

import java.util.HashSet;
import java.util.Scanner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class DefaultCommandInterpreterTest {

    @Test
    public void nextCommandShouldReturnPlayerCommandGivenValidVerbAndNoun() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Verb("JUMP"))), new HashSet<>(asList(new Noun("UP"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("JUMP UP"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().matches(new Verb("JUMP")));
        assertTrue(command.getNoun().matches(new Noun("UP")));
    }

    @Test
    public void nextCommandShouldRecognizeVerbGivenSynonym() {
        Verb jump = new Verb("JUMP", "J");
        Noun up = new Noun("UP", "u");
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(jump)), new HashSet<>(asList(up)));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("j up"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().toString(), command.getVerb().matches(jump));
        assertTrue(command.getNoun().toString(), command.getNoun().matches(up));
    }

    @Test
    public void nextCommandShouldReturnUnrecognizedGivenVerbOrNounNotInVocabulary() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Verb("JUMP"))), new HashSet<>(asList(new Noun("UP"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("KILL TROLL"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().equals(Verb.UNRECOGNIZED));
        assertTrue(command.getNoun().equals(Noun.UNRECOGNIZED));
    }

    @Test
    public void firstTermShouldBeRecognizedAsNounGivenItIsNotVerb() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Verb("GO"))), new HashSet<>(asList(new Noun("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("NORTH"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().equals(Verb.NONE));
        assertTrue(command.getNoun().matches(new Noun("north")));
    }
}