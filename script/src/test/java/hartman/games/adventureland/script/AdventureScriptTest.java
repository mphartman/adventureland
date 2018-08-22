package hartman.games.adventureland.script;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Game;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.core.DefaultCommandInterpreter;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;

public class AdventureScriptTest {

    private class EchoCommandInterpreter extends DefaultCommandInterpreter {

        private final Display display;

        private EchoCommandInterpreter(Scanner scanner, Vocabulary vocabulary, Display display) {
            super(scanner, vocabulary);
            this.display = display;
        }

        @Override
        public Command nextCommand() {
            Command command = super.nextCommand();
            display.print(String.format("> %s (%s)%n%n", getLastLine(), command.toString()));
            return command;
        }
    }

    private class TranscriptCommandInterpreter extends EchoCommandInterpreter {
        private TranscriptCommandInterpreter(String path, Vocabulary vocabulary, Display display) {
            super(new Scanner(TranscriptCommandInterpreter.class.getResourceAsStream(path)), vocabulary, display);
        }
    }

    private class SimpleTestDisplay extends TestDisplay {

        @Override
        public void look(Room room, List<Item> itemsInRoom) {
            StringBuilder builder = new StringBuilder();
            builder.append(room.getDescription()).append(NEWLINE);
            if (!room.getExits().isEmpty()) {
                builder.append("Room exits: ")
                        .append(room.getExits().stream().map(Room.Exit::getDescription).collect(joining(", ")))
                        .append(NEWLINE);
            }
            if (!itemsInRoom.isEmpty()) {
                builder.append("Room items: ")
                        .append(itemsInRoom.stream().map(Item::getDescription).collect(joining(", ")))
                        .append(NEWLINE);
            }
            print(builder.toString());
        }

        @Override
        public void inventory(List<Item> itemsCarried) {
            if (itemsCarried.isEmpty()) {
                print("Inventory is empty." + NEWLINE);
            } else {
                print("Inventory items: ");
                print(itemsCarried.stream().map(Item::getDescription).collect(joining(", ")) + NEWLINE);
            }
        }

    }

    private static final String NEWLINE = System.getProperty("line.separator");

    private static Adventure readAdventure(String path) throws IOException {
        try (Reader r = new InputStreamReader(AdventureScriptTest.class.getResourceAsStream(path), StandardCharsets.UTF_8)) {
            return new AdventureScriptParserImpl().parse(r);
        }
    }

    private static String readToString(String path) {
        try (Scanner scanner = new Scanner(AdventureScriptTest.class.getResourceAsStream(path), "UTF-8").useDelimiter("\\A")) {
            if (scanner.hasNext()) {
                return scanner.next();
            }
            return "";
        }
    }

    private void testAdventure(int id) {
        TestDisplay display = new SimpleTestDisplay();
        try {
            String ident = String.format("%03d", id);
            Adventure adventure = readAdventure(String.format("/adventures/%s/adventure.txt", ident));
            CommandInterpreter interpreter = new TranscriptCommandInterpreter(String.format("/adventures/%s/input.txt", ident), adventure.getVocabulary(), display);
            Game game = new Game(adventure, interpreter, display);
            GameState gameState = new GameState(adventure.getStartRoom(), adventure.getItems());
            game.run(gameState);
            String expected = readToString(String.format("/adventures/%s/transcript.txt", ident));
            String actual = display.toString();
            assertEquals(expected, actual);
        } catch (Exception e) {
            System.err.println(display.toString());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAdventure1() {
        testAdventure(1);
    }

    @Test
    public void testAdventure2() {
        testAdventure(2);
    }

    @Test
    public void testAdventure3() {
        testAdventure(3);
    }

}
