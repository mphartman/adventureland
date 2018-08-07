package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.*;

import java.util.Scanner;

public class DefaultInterpreter implements Interpreter {

    private final Scanner scanner;
    private final Vocabulary vocabulary;

    public DefaultInterpreter(Scanner scanner, Vocabulary vocabulary) {
        this.scanner = scanner;
        this.vocabulary = vocabulary;
    }

    @Override
    public Command nextCommand() {

        String line = scanner.nextLine();
        Scanner lineScanner = new Scanner(line);

        Verb verb = Verb.NONE;
        Noun noun = Noun.NONE;

        if (lineScanner.hasNext()) {
            String firstTerm = lineScanner.next();
            verb = vocabulary.toVerb(firstTerm).orElse(Verb.UNRECOGNIZED);

            if (lineScanner.hasNext()) {
                String secondTerm = lineScanner.next();
                noun = vocabulary.toNoun(secondTerm).orElse(Noun.UNRECOGNIZED);
            }
        }

        Command command = new Command(verb, noun);

//        System.out.printf("I heard you say: \"%s\"%n", line);
//        System.out.printf("And I understood it as  %s%n", command);

        return command;
    }
}
