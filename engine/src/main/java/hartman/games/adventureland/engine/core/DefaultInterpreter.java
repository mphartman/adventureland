package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.*;

import java.util.Scanner;

public class DefaultInterpreter implements Interpreter {

    private final Scanner scanner;

    private Vocabulary vocabulary;

    public DefaultInterpreter(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    @Override
    public PlayerCommand nextCommand() {
        String possibleVerb = scanner.next();
        String possibleNoun = scanner.next();
        return new PlayerCommand(
                vocabulary.toVerb(possibleVerb).orElse(Verb.UNRECOGNIZED),
                vocabulary.toNoun(possibleNoun).orElse(Noun.UNRECOGNIZED));
    }
}
