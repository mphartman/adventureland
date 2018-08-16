package hartman.games.adventureland.app;

import hartman.games.adventureland.adventures.HouseEscapeAdventure;
import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Game;
import hartman.games.adventureland.engine.GameState;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.String.format;

@SpringBootApplication
public class AdventurelandApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AdventurelandApplication.class, args);
    }

    private static final String introduction = format(
            "House Escape, a text-based adventure.%n" +
                    "Copyright © 2018, Michael Hartman%n" +
                    "Distributed under the Apache License, version 2.%n%n" +
                    "A voice BOOMS out:%n" +
                    "\"In this adventure you're to escape from the haunted house.\"%n%n" +
                    "Remember you can always say \"HELP\"%n"
    );


    @Override
    public void run(String... args) {
        Adventure adventure = HouseEscapeAdventure.adventure();
        CommandInterpreter interpreter = new ConsoleInterpreter(adventure.getVocabulary());
        ConsoleDisplay display = new ConsoleDisplay();
        GameState gameState = new GameState(adventure.getStartRoom(), adventure.getItems());
        Game game = new Game(adventure, interpreter, display);
        display.print(introduction);
        game.run(gameState);
    }
}

