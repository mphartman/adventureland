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

    public DefaultCommandInterpreter(Scanner scanner, Vocabulary vocabulary) {
        this.scanner = scanner;
        this.vocabulary = vocabulary;
    }

    @Override
    public Command nextCommand() {
        String line = scanner.nextLine();
        try (Scanner lineScanner = new Scanner(line)) {
            LinkedList<Word> words = new LinkedList<>();
            while (lineScanner.hasNext()) {
                words.add(vocabulary.findMatch(lineScanner.next()).orElse(Word.UNRECOGNIZED));
            }
            Word firstWord = pop(words, Word.NONE);
            Word secondWord = pop(words, Word.NONE);
            return new Command(firstWord, secondWord);
        }
    }

    private Word pop(LinkedList<Word> words, Word defaultWord) {
        if (words.isEmpty()) return defaultWord;
        return words.removeFirst();
    }
}
