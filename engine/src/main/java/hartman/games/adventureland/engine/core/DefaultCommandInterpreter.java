package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Command;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;

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

            Verb verb = Verb.NONE;
            Noun noun = Noun.NONE;

            if (lineScanner.hasNext()) {
                String firstTerm = lineScanner.next();

                Optional<Verb> maybeVerb = vocabulary.find(new Verb(firstTerm));
                if (maybeVerb.isPresent()) {

                    verb = maybeVerb.get();

                    if (lineScanner.hasNext()) {
                        noun = vocabulary.find(new Noun(lineScanner.next())).orElse(Noun.UNRECOGNIZED);
                    }

                } else {
                    Optional<Noun> maybeNoun = vocabulary.find(new Noun(firstTerm));
                    if (maybeNoun.isPresent()) {
                        noun = maybeNoun.get();
                    }
                    else {
                        verb = Verb.UNRECOGNIZED;
                        if (lineScanner.hasNext()) {
                            noun = vocabulary.find(new Noun(lineScanner.next())).orElse(Noun.UNRECOGNIZED);
                        }
                    }
                }
            }

            return new Command(verb, noun);
        }
    }
}
