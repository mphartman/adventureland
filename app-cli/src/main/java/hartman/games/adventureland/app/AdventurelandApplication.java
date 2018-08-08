package hartman.games.adventureland.app;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Game;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Conditions;
import hartman.games.adventureland.engine.core.DefaultCommandInterpreter;
import hartman.games.adventureland.engine.core.Results;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static hartman.games.adventureland.engine.Action.Result;
import static hartman.games.adventureland.engine.Action.setOf;
import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Nouns.EAST;
import static hartman.games.adventureland.engine.core.Nouns.NORTH;
import static hartman.games.adventureland.engine.core.Nouns.SOUTH;
import static hartman.games.adventureland.engine.core.Nouns.UP;
import static hartman.games.adventureland.engine.core.Nouns.WEST;
import static hartman.games.adventureland.engine.core.Nouns.directions;
import static hartman.games.adventureland.engine.core.Verbs.GO;
import static hartman.games.adventureland.engine.core.Verbs.LOOK;
import static hartman.games.adventureland.engine.core.Verbs.QUIT;

@SpringBootApplication
public class AdventurelandApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AdventurelandApplication.class, args);
    }

    private static final String introduction = String.format(
            "House Escape, a text-based adventure.%n" +
                    "Copyright © 2018, Michael Hartman%n" +
                    "Distributed under the Apache License, version 2.%n%n" +
                    "A voice BOOMS out:%n" +
                    "\"In this adventure you're to escape from the haunted house.  Good luck.\"%n%n"
    );


    @Override
    public void run(String... args) {
        Adventure adventure = MyAdventures.House_Escape();
        CommandInterpreter interpreter = new ConsoleInterpreter(adventure.getVocabulary());
        GameState gameState = new GameState(adventure.getStartRoom(), adventure.getItems());
        ConsoleDisplay display = new ConsoleDisplay();
        display.print(introduction);
        Game game = new Game(adventure, interpreter, gameState, display);
        game.run();
        display.print(String.format("%n%nGoodbye. Thank you for playing. Have a nice day!%n"));
    }
}

class MyAdventures {

    static Adventure House_Escape() {

        Set<Verb> verbs = Vocabulary.setOf(QUIT, GO, LOOK);
        Set<Noun> nouns = directions();
        Vocabulary vocabulary = new Vocabulary(verbs, nouns);

        Room hallway = new Room("hallway", String.format("I'm in a short, narrow hallway.%nThere's a short flight of stairs going up.%nThe hallway continues to the south."));
        Room upper_stairs = new Room("upper_stairs", "I'm on the top of the stairs.");
        Room bedroom = new Room("bedroom", "I'm in the master bedroom.");
        Room kitchen = new Room("kitchen", "I'm the kitchen. The way south appears to be the way!");
        Room living_room = new Room("living_room", "I'm in a living room with old couches.");
        Room outside = new Room("outside", "I'm outside the house.");
        hallway.setExit(UP, upper_stairs);
        hallway.setExit(SOUTH, kitchen);
        upper_stairs.setExit(DOWN, hallway);
        upper_stairs.setExit(SOUTH, bedroom);
        bedroom.setExit(NORTH, upper_stairs);
        kitchen.setExit(NORTH, hallway);
        kitchen.setExit(WEST, living_room);
        living_room.setExit(EAST, kitchen);
        kitchen.setExit(SOUTH, outside);

        Action introOccurs = new Action(setOf(new Conditions.TIMES(1)), setOf(DO_LOOK));
        Action promptOccurs = new Action(setOf(new Conditions.NOT(new Conditions.IN_ROOM(outside))), setOf(new Results.PRINT(String.format("%nWhat should I do? "))));
        Action gameOverOccurs = new Action(setOf(new Conditions.IN_ROOM(outside)), setOf(new Results.PRINT(String.format("%n*** Congratulations, you've escaped!***")), Results.QUIT));
        Set<Action> occurs = new LinkedHashSet<>(Arrays.asList(introOccurs, promptOccurs, gameOverOccurs));

        Action goAction = new Action(GO, Noun.ANY, setOf(Conditions.HAS_EXIT), setOf(Results.GOTO, DO_LOOK));
        Action lookAction = new Action(LOOK, Noun.ANY, DO_LOOK);
        Action unrecognizedVerbAction = new Action(Verb.UNRECOGNIZED, new Results.PRINT("Huh? I don't know how to do that. "));
        Action unrecognizedVerbAndNounAction = new Action(Verb.UNRECOGNIZED, Noun.UNRECOGNIZED, new Results.PRINT("I don't know how to do that with that. "));
        Set<Action> actions = new LinkedHashSet<>(Arrays.asList(Actions.QUIT_ACTION, goAction, lookAction, unrecognizedVerbAction, unrecognizedVerbAndNounAction));

        Set<Item> items = Collections.emptySet();

        return new Adventure(vocabulary, occurs, actions, items, hallway);
    }

    private static Result DO_LOOK = new Results.LOOK((room, exits, items) -> {
        StringBuilder buf = new StringBuilder();
        buf.append(String.format("%n%s%n", room.getDescription()));
        if (exits.size() == 0) {
            buf.append(String.format("There are no obvious exits from here.%n"));
        } else {
            if (exits.size() == 1) {
                buf.append(String.format("There is a single exit which goes %s%n", exits.get(0).getDescription()));
            } else {
                String exitsString = String.join(", ", exits.stream().map(Room.Exit::getDescription).collect(Collectors.toList()));
                buf.append(String.format("There are %d exits: %s%n", exits.size(), exitsString));
            }
        }
        return buf.toString();
    });

}

class ConsoleInterpreter extends DefaultCommandInterpreter {
    public ConsoleInterpreter(Vocabulary vocabulary) {
        super(new Scanner(System.in), vocabulary);
    }
}

class ConsoleDisplay implements Display {
    @Override
    public void print(String message) {
        System.out.print(message);
    }
}