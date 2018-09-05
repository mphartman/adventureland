package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Game;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.core.DefaultCommandInterpreter;
import hartman.games.adventureland.engine.core.DefaultDisplay;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class AdventureScriptTest {

    private class TestCommandInterpreter extends DefaultCommandInterpreter {

        private final Display display;

        private TestCommandInterpreter(String path, Vocabulary vocabulary, Display display) {
            super(new Scanner(TestCommandInterpreter.class.getResourceAsStream(path)), vocabulary);
            this.display = display;
        }

        @Override
        public Command nextCommand() {
            Command command = super.nextCommand();
            display.print(String.format("> %s (%s)%n%n", getLastLine(), command.toString()));
            return command;
        }
    }

    private Adventure readAdventure(InputStream inputStream) throws IOException {
        try (Reader r = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return new AdventureScriptParserImpl().parse(r);
        }
    }

    private String readToString(InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A")) {
            if (scanner.hasNext()) {
                return scanner.next();
            }
            return "";
        }
    }

    private void runAdventure(int id) {
        StringWriter out = new StringWriter();
        try (PrintWriter pw = new PrintWriter(out)) {
            try {
                String ident = String.format("%03d", id);
                Adventure adventure = readAdventure(getClass().getResourceAsStream(String.format("/adventures/%s/adventure.txt", ident)));
                Display display = new DefaultDisplay(pw);
                CommandInterpreter interpreter = new TestCommandInterpreter(String.format("/adventures/%s/input.txt", ident), adventure.getVocabulary(), display);
                GameState gameState = new GameState(adventure.getStartRoom(), adventure.getItems());
                Game game = new Game(adventure, interpreter, display, gameState);
                game.run();
                String expected = readToString(getClass().getResourceAsStream(String.format("/adventures/%s/transcript.txt", ident)));
                String actual = out.toString();
                assertEquals(expected, actual);
            } catch (Exception e) {
                System.err.println(out.toString());
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testAdventure1() {
        runAdventure(1);
    }

    @Test
    public void testAdventure2() {
        runAdventure(2);
    }

    @Test
    public void testAdventure3() {
        runAdventure(3);
    }

    @Test
    public void testAdventure4() {
        runAdventure(4);
    }

    @Test
    public void testAdventure5() {
        runAdventure(5);
    }

}
