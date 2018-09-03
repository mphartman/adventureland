package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;

import java.util.LinkedList;
import java.util.Scanner;

public class DefaultCommandInterpreter implements CommandInterpreter {

    private final Scanner scanner;
    private final Vocabulary vocabulary;

    private String lastLine;

    public DefaultCommandInterpreter(Scanner scanner, Vocabulary vocabulary) {
        this.scanner = scanner;
        this.vocabulary = vocabulary;
    }

    @Override
    public Command nextCommand() {

        // skip blank lines
        String line = null;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (!line.isEmpty()) {
                break;
            }
        }

        if (!(null == line || line.isEmpty())) {
            lastLine = line;
            try (Scanner lineScanner = new Scanner(lastLine)) {
                LinkedList<Word> words = new LinkedList<>();
                while (lineScanner.hasNext()) {
                    String next = lineScanner.next();
                    words.add(vocabulary.findMatch(next).orElse(Word.unrecognized(next)));
                }
                return new Command(words.toArray(new Word[0]));
            }
        }

        return Command.NONE;
    }

    protected String getLastLine() {
        return lastLine;
    }

}
