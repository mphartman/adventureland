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
    public void nextCommandShouldReturnCommandGivenValidWords() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JUMP"), (new Word("UP")))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("JUMP UP"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().matches(new Word("JUMP")));
        assertTrue(command.getSecondWord().matches(new Word("UP")));
    }

    @Test
    public void nextCommandShouldRecognizeWordGivenItsSynonym() {
        Word jump = new Word("JUMP", "J");
        Word up = new Word("UP", "u");
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(jump, up)));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("j up"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().toString(), command.getFirstWord().matches(jump));
        assertTrue(command.getSecondWord().toString(), command.getSecondWord().matches(up));
    }

    @Test
    public void nextCommandShouldReturnUnrecognizedGivenWordsNotInVocabulary() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JUMP"), new Word("UP"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("KILL TROLL"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().equals(Word.UNRECOGNIZED));
        assertTrue(command.getSecondWord().equals(Word.UNRECOGNIZED));
    }

    @Test
    public void noInput_NoneNone() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("\n"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().matches(Word.NONE));
        assertTrue(command.getSecondWord().matches(Word.NONE));
    }

    @Test
    public void oneWordInput_Recognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("NORTH"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().matches(new Word("NORTH")));
        assertTrue(command.getSecondWord().matches(Word.NONE));
    }

    @Test
    public void oneWordInput_Unrecognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("BANANAS"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().matches(Word.UNRECOGNIZED));
        assertTrue(command.getSecondWord().matches(Word.NONE));
    }

    @Test
    public void twoWordInput_OnlyFirstRecognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("NORTH PAJAMAS"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().matches(new Word("NORTH")));
        assertTrue(command.getSecondWord().matches(Word.UNRECOGNIZED));
    }

    @Test
    public void twoWordInput_BothRecognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("NORTH JAVELIN"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().matches(new Word("NORTH")));
        assertTrue(command.getSecondWord().matches(new Word("JAVELIN")));
    }

    @Test
    public void twoWordInput_NeitherRecognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("curious george"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().matches(Word.UNRECOGNIZED));
        assertTrue(command.getSecondWord().matches(Word.UNRECOGNIZED));
    }

    @Test
    public void multipleWordInput_FirstTwoWordsAllThatMatter() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("throw"), new Word("axe"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("throw axe at bear"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getFirstWord().matches(new Word("THROW")));
        assertTrue(command.getSecondWord().matches(new Word("AXE")));
    }


}