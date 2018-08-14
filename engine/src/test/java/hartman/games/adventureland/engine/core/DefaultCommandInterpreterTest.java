package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import org.junit.Test;

import java.util.HashSet;
import java.util.Scanner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

public class DefaultCommandInterpreterTest {

    @Test
    public void nextCommandShouldReturnPlayerCommandGivenValidVerbAndNoun() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JUMP"))), new HashSet<>(asList(new Word("UP"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("JUMP UP"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().matches(new Word("JUMP")));
        assertTrue(command.getNoun().matches(new Word("UP")));
    }

    @Test
    public void nextCommandShouldRecognizeVerbGivenSynonym() {
        Word jump = new Word("JUMP", "J");
        Word up = new Word("UP", "u");
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(jump)), new HashSet<>(asList(up)));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("j up"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().toString(), command.getVerb().matches(jump));
        assertTrue(command.getNoun().toString(), command.getNoun().matches(up));
    }

    @Test
    public void nextCommandShouldReturnUnrecognizedGivenVerbOrNounNotInVocabulary() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JUMP"))), new HashSet<>(asList(new Word("UP"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("KILL TROLL"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().equals(Word.UNRECOGNIZED));
        assertTrue(command.getNoun().equals(Word.UNRECOGNIZED));
    }

    @Test
    public void firstTermShouldBeRecognizedAsNounGivenItIsNotVerb() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("GO"))), new HashSet<>(asList(new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("NORTH"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getVerb().equals(Word.NONE));
        assertTrue(command.getNoun().matches(new Word("north")));
    }
}