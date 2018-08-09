package hartman.games.adventureland.app;

import hartman.games.adventureland.engine.Display;

class ConsoleDisplay implements Display {
    @Override
    public void print(String message) {
        System.out.print(message);
    }
}
