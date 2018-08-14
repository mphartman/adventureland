package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;

import java.util.Optional;
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

            Word verb = Word.NONE;
            Word noun = Word.NONE;

            if (lineScanner.hasNext()) {
                String firstTerm = lineScanner.next();

                Optional<Word> maybeVerb = vocabulary.findMatchingVerb(new Word(firstTerm));
                if (maybeVerb.isPresent()) {

                    verb = maybeVerb.get();

                    if (lineScanner.hasNext()) {
                        noun = vocabulary.findMatchingNoun(new Word(lineScanner.next())).orElse(Word.UNRECOGNIZED);
                    }

                } else {
                    Optional<Word> maybeNoun = vocabulary.findMatchingNoun(new Word(firstTerm));
                    if (maybeNoun.isPresent()) {
                        noun = maybeNoun.get();
                    }
                    else {
                        verb = Word.UNRECOGNIZED;
                        if (lineScanner.hasNext()) {
                            noun = vocabulary.findMatchingNoun(new Word(lineScanner.next())).orElse(Word.UNRECOGNIZED);
                        }
                    }
                }
            }

            return new Command(verb, noun);
        }
    }
}
