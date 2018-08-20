package hartman.games.adventureland.app;

import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.core.DefaultCommandInterpreter;

import java.util.Scanner;

class ConsoleInterpreter extends DefaultCommandInterpreter {
    public ConsoleInterpreter(Vocabulary vocabulary) {
        super(new Scanner(System.in), vocabulary);
    }
}
