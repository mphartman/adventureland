package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import org.junit.Test;

import java.util.HashSet;
import java.util.Scanner;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultCommandInterpreterTest {

    @Test
    public void nextCommandShouldReturnCommandGivenValidWords() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JUMP"), (new Word("UP")))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("JUMP UP"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getWordOrNone(1).matches(new Word("JUMP")));
        assertTrue(command.getWordOrNone(2).matches(new Word("UP")));
    }

    @Test
    public void nextCommandShouldRecognizeWordGivenItsSynonym() {
        Word jump = new Word("JUMP", "J");
        Word up = new Word("UP", "u");
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(jump, up)));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("j up"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getWordOrNone(1).toString(), command.getWordOrNone(1).matches(jump));
        assertTrue(command.getWordOrNone(2).toString(), command.getWordOrNone(2).matches(up));
    }

    @Test
    public void nextCommandShouldReturnUnrecognizedGivenWordsNotInVocabulary() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JUMP"), new Word("UP"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("KILL TROLL"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getWordOrNone(1).isUnrecognized());
        assertTrue(command.getWordOrNone(2).isUnrecognized());
    }

    @Test
    public void noInput_BlankLinesConsumed_ReturnsCommandNone() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("\n\n\n\n"), vocabulary);
        Command command = interpreter.nextCommand();
        assertEquals(Command.NONE, command);
    }

    @Test
    public void oneWordInput_Recognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("NORTH"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getWordOrNone(1).matches(new Word("NORTH")));
        assertTrue(command.getWordOrNone(2).matches(Word.NONE));
    }

    @Test
    public void oneWordInput_Unrecognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("BANANAS"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getWordOrNone(1).isUnrecognized());
        assertTrue(command.getWordOrNone(2).matches(Word.NONE));
    }

    @Test
    public void twoWordInput_OnlyFirstRecognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("NORTH PAJAMAS"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getWordOrNone(1).matches(new Word("NORTH")));
        assertTrue(command.getWordOrNone(2).isUnrecognized());
    }

    @Test
    public void twoWordInput_BothRecognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("NORTH JAVELIN"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getWordOrNone(1).matches(new Word("NORTH")));
        assertTrue(command.getWordOrNone(2).matches(new Word("JAVELIN")));
    }

    @Test
    public void twoWordInput_NeitherRecognized() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("JAVELIN"), new Word("NORTH"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("curious george"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getWordOrNone(1).isUnrecognized());
        assertTrue(command.getWordOrNone(2).isUnrecognized());
    }

    @Test
    public void multipleWordInput() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("throw"), new Word("axe"), new Word("at"), new Word("bear"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("throw axe at bear"), vocabulary);
        Command command = interpreter.nextCommand();
        assertTrue(command.getWordOrNone(1).matches(new Word("THROW")));
        assertTrue(command.getWordOrNone(2).matches(new Word("AXE")));
        assertTrue(command.getWord(3).get().matches(new Word("at")));
        assertTrue(command.getWord(4).get().matches(new Word("bear")));
    }

    @Test
    public void lastLineProvidesRawInput() {
        Vocabulary vocabulary = new Vocabulary(new HashSet<>(asList(new Word("throw"), new Word("axe"))));
        DefaultCommandInterpreter interpreter = new DefaultCommandInterpreter(new Scanner("throw axe at bear"), vocabulary);
        interpreter.nextCommand();
        assertEquals("throw axe at bear", interpreter.getLastLine());
    }


}