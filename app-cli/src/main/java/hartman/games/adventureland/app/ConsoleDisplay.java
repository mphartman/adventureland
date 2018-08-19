package hartman.games.adventureland.app;

import hartman.games.adventureland.engine.core.DefaultDisplay;

import java.io.PrintWriter;

public class ConsoleDisplay extends DefaultDisplay {

    public ConsoleDisplay() {
        super(new PrintWriter(System.out));
    }

}
