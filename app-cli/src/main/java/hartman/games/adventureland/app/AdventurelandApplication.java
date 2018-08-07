package hartman.games.adventureland.app;

import hartman.games.adventureland.engine.*;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.core.*;
import hartman.games.adventureland.engine.core.DefaultCommandInterpreter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@SpringBootApplication
public class AdventurelandApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AdventurelandApplication.class, args);
    }

    private static final String introduction = String.format(
            "House Escape, a text-based adventure.%n" +
                    "Copyright © 2018, Michael Hartman%n" +
                    "Distributed under the Apache License, version 2.%n%n" +
                    "A voice BOOMS out:%n%n" +
                    "In this adventure you're to escape from the haunted house.  Good luck.%n"
    );


    @Override
    public void run(String... args) {
        Adventure adventure = MyAdventures.House_Escape();
        CommandInterpreter interpreter = new ConsoleCommandInterpreter(adventure.getVocabulary());
        GameState gameState = new GameState(adventure.getStartRoom());
        ConsoleDisplay display = new ConsoleDisplay();
        display.print(introduction);
        Game game = new Game(adventure, interpreter, gameState, display);
        game.run();
        display.print("Goodbye. Thank you for playing. Have a nice day!");
    }
}

class MyAdventures {

    static Adventure House_Escape() {

        Set<Verb> verbs = Verbs.asSet(Verbs.QUIT, Verbs.GO, Verbs.LOOK);
        Set<Noun> nouns = Nouns.directions();
        Vocabulary vocabulary = new Vocabulary(verbs, nouns);

        Room hallway = new Room("hallway", "I'm in a short, narrow hallway.");
        Room upper_stairs = new Room("upper_stairs", "I'm on a set of stairs leading to the upper floor.");
        Room bedroom = new Room("bedroom", "I'm in the master bedroom.");
        Room kitchen = new Room("kitchen", "I'm the kitchen.");
        Room living_room = new Room("living_room", "I'm in a living room with old couches.");
        hallway.setExit(Nouns.UP, upper_stairs);
        hallway.setExit(Nouns.SOUTH, kitchen);
        upper_stairs.setExit(Nouns.DOWN, hallway);
        upper_stairs.setExit(Nouns.SOUTH, bedroom);
        bedroom.setExit(Nouns.NORTH, upper_stairs);
        kitchen.setExit(Nouns.NORTH, hallway);
        kitchen.setExit(Nouns.WEST, living_room);
        living_room.setExit(Nouns.EAST, kitchen);

        Action promptAction = new Action(new Results.PRINT("What should I do?  "));
        Set<Action> occurs = new HashSet<>(Arrays.asList(promptAction));

        Set<Action> actions = new HashSet<>(Arrays.asList(Actions.QUIT_ACTION, Actions.GO_ACTION, Actions.LOOK_ACTION));

        return new Adventure(vocabulary, occurs, actions, hallway);
    }
}

class ConsoleCommandInterpreter extends DefaultCommandInterpreter {
    public ConsoleCommandInterpreter(Vocabulary vocabulary) {
        super(new Scanner(System.in), vocabulary);
    }
}

class ConsoleDisplay implements Display {
    @Override
    public void print(String message) {
        System.out.print(message);
    }
}