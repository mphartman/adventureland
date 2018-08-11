package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;

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

            Verb verb = Verb.NONE;
            Noun noun = Noun.NONE;

            if (lineScanner.hasNext()) {
                String firstTerm = lineScanner.next();
                verb = vocabulary.findVerb(firstTerm).orElse(Verb.UNRECOGNIZED);

                if (lineScanner.hasNext()) {
                    String secondTerm = lineScanner.next();
                    noun = vocabulary.findNoun(secondTerm).orElse(Noun.UNRECOGNIZED);
                }
            }

            return new Command(verb, noun);
        }
    }
}
