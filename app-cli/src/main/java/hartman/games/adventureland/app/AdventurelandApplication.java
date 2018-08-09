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
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static hartman.games.adventureland.engine.Action.Result;
import static hartman.games.adventureland.engine.Action.setOf;
import static hartman.games.adventureland.engine.core.Conditions.*;
import static hartman.games.adventureland.engine.core.Conditions.HasExit;
import static hartman.games.adventureland.engine.core.Conditions.InRoom;
import static hartman.games.adventureland.engine.core.Conditions.IsPresent;
import static hartman.games.adventureland.engine.core.Conditions.ItemHere;
import static hartman.games.adventureland.engine.core.Conditions.Not;
import static hartman.games.adventureland.engine.core.Conditions.Random;
import static hartman.games.adventureland.engine.core.Conditions.Times;
import static hartman.games.adventureland.engine.core.Nouns.DOOR;
import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Nouns.EAST;
import static hartman.games.adventureland.engine.core.Nouns.NORTH;
import static hartman.games.adventureland.engine.core.Nouns.SOUTH;
import static hartman.games.adventureland.engine.core.Nouns.UP;
import static hartman.games.adventureland.engine.core.Nouns.WEST;
import static hartman.games.adventureland.engine.core.Nouns.directions;
import static hartman.games.adventureland.engine.core.Results.Drop;
import static hartman.games.adventureland.engine.core.Results.Get;
import static hartman.games.adventureland.engine.core.Results.Go;
import static hartman.games.adventureland.engine.core.Results.Goto;
import static hartman.games.adventureland.engine.core.Results.Inventory;
import static hartman.games.adventureland.engine.core.Results.Look;
import static hartman.games.adventureland.engine.core.Results.Print;
import static hartman.games.adventureland.engine.core.Results.Quit;
import static hartman.games.adventureland.engine.core.Results.Swap;
import static hartman.games.adventureland.engine.core.Verbs.DROP;
import static hartman.games.adventureland.engine.core.Verbs.GET;
import static hartman.games.adventureland.engine.core.Verbs.GO;
import static hartman.games.adventureland.engine.core.Verbs.INVENTORY;
import static hartman.games.adventureland.engine.core.Verbs.LOOK;
import static hartman.games.adventureland.engine.core.Verbs.OPEN;
import static hartman.games.adventureland.engine.core.Verbs.QUIT;
import static hartman.games.adventureland.engine.core.Verbs.USE;
import static java.lang.String.format;
import static java.lang.String.join;

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
        display.print(format("%n%nThank you for playing. Have a nice day!%n"));
    }
}

class MyAdventures {

    static Adventure House_Escape() {

        Verb kill = new Verb("KILL", "SWAT", "HIT");
        Verb yell = new Verb("YELL", "SHOUT", "SCREAM");
        Noun fly = new Noun("FLY", "BUG", "INSECT", "PEST");

        Room hallway = new Room("hallway", format("I'm in a short, narrow hallway.%nThere's a short flight of stairs going up.%nThe hallway continues to the south."));
        Room upper_stairs = new Room("upper_stairs", "I'm on the top of the stairs.");
        Room bedroom = new Room("bedroom", "I'm in the master bedroom.");
        Room kitchen = new Room("kitchen", "I'm the kitchen.");
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

        Item key = Item.newPortableObjectItem("key", "A brass key.", bedroom);
        Item lockedDoor = Item.newSceneryRoomItem("locked_door", "Locked door.", kitchen);
        Item openDoor = Item.newSceneryRoomItem("open_door", "An unlocked door.");
        Item flyswatter = Item.newPortableObjectItem("flyswatter", "A messy, green flyswatter", living_room);
        Set<Item> items = new LinkedHashSet<>(Arrays.asList(key, lockedDoor, openDoor, flyswatter));

        Action initialLookOccurs = new Action(setOf(new Times(1)), setOf(Look));
        Action promptOccurs = new Action(setOf(new Not(new InRoom(outside))), setOf(new Print(format("%nWhat should I do? "))));
        Action gameOverOccurs = new Action(setOf(InRoom.of(outside)), setOf(new Print(format("%n*** Congratulations, you've escaped!***")), Quit));
        Action flyInTheKitchen = new Action(setOf(InRoom.of(kitchen), Random.of(60)), setOf(Print.of(format("A fly buzzes past my ear.%n"))));
        Set<Action> occurs = new LinkedHashSet<>(Arrays.asList(initialLookOccurs, flyInTheKitchen, promptOccurs, gameOverOccurs));

        /*
         *   Standard actions
         */
        Action goAction = new Action(GO, Noun.ANY, setOf(HasExit), setOf(Go, Look));
        Action lookAction = new Action(LOOK, Noun.ANY, Look);
        Action inventoryAction = new Action(INVENTORY, Noun.ANY, Inventory);
        Action badVerbAlone = new Action(Verb.UNRECOGNIZED, new Print("Sorry, I don't know how to do that. "));
        Action badVerbAnyNoun = new Action(Verb.UNRECOGNIZED, Noun.ANY, new Print("Nope, I don't understand. "));
        Action badVerbAndNoun = new Action(Verb.UNRECOGNIZED, Noun.UNRECOGNIZED, new Print("I don't know how to do that with that. "));
        Action badNoun = new Action(Verb.ANY, Noun.UNRECOGNIZED, new Print("I don't recognize that noun. "));
        Action missingNoun = new Action(Verb.ANY, Noun.NONE, new Print("I need more information. "));
        Action defaultAction = new Action(Verb.ANY, Noun.ANY, Print.of("Huh? I didn't catch that. "));
        Set<Action> standardActions = new LinkedHashSet<>(Arrays.asList(Actions.QuitAction, goAction, lookAction, inventoryAction, badVerbAlone, badVerbAndNoun, badNoun, missingNoun, badVerbAnyNoun, defaultAction));

        /*
         *   Adventure actions
         */
        Action openLockedDoorWithoutKey = new Action(OPEN, DOOR, setOf(ItemHere.of(lockedDoor), Not.of(IsPresent.of(key))), setOf(Print.of(format("It's locked. I need some way to unlock it.%n"))));
        Action goLockedDoor = new Action(GO, DOOR, setOf(ItemHere.of(lockedDoor)), setOf(Print.of(format("I can't. It's locked.%n"))));
        Action getKey = new Action(GET, key.asNoun(), setOf(ItemHere.of(key)), setOf(Get, Print.of(format("Okay. I got the key. Type INVENTORY to see what I'm carrying.%n"))));
        Action dropKey = new Action(DROP, key.asNoun(), setOf(ItemCarried.of(key)), setOf(Drop, Print.of(format("I dropped the key.%n"))));
        Action openLockedDoorWithKey = new Action(OPEN, DOOR, setOf(ItemHere.of(lockedDoor), IsPresent.of(key)), setOf(Swap.of(lockedDoor, openDoor), Print.of(format("<CLICK> That did it. It's unlocked.%n")), Look));
        Action useKeyOnLockedDoor = new Action(USE, key.asNoun(), setOf(ItemHere.of(lockedDoor), IsPresent.of(key)), setOf(Swap.of(lockedDoor, openDoor), Print.of(format("<CLICK> That did it. It's unlocked.%n")), Look));
        Action goDoor = new Action(GO, DOOR, setOf(ItemHere.of(openDoor)), setOf(Goto.of(outside), Print.of(format("Yeah! I've made it outside!%n"))));
        Action getFlySwatter = new Action(GET, flyswatter.asNoun(), setOf(ItemHere.of(flyswatter)), setOf(Get, Print.of(format("Gross, but okay, I got it.%n"))));
        Action killFly = new Action(kill, fly, setOf(ItemCarried.of(flyswatter)), setOf(Print.of(format("I got 'em.%n"))));
        Action yellAction = Action.Builder.newBuilder().verb(yell).then(Print.of("You don't have to yell. I can hear you.")).build();
        Set<Action> adventureActions = new LinkedHashSet<>(Arrays.asList(openLockedDoorWithKey, useKeyOnLockedDoor, openLockedDoorWithoutKey, goLockedDoor, goDoor, getKey, dropKey, killFly, getFlySwatter, yellAction));

        Set<Action> actions = new LinkedHashSet<>();
        actions.addAll(adventureActions);
        actions.addAll(standardActions);

        Set<Verb> verbs = Vocabulary.setOf(QUIT, INVENTORY, GO, LOOK, OPEN, GET, DROP, kill, USE, yell);
        Set<Noun> nouns = Vocabulary.setOf(DOOR, key.asNoun(), flyswatter.asNoun(), fly);
        nouns.addAll(directions());
        Vocabulary vocabulary = new Vocabulary(verbs, nouns);

        return new Adventure(vocabulary, occurs, actions, items, hallway);
    }

    private static Result Look = new Look((room, exits, items) -> {
        StringBuilder buf = new StringBuilder();
        buf.append(format("%s%n", room.getDescription()));
        if (exits.size() == 0) {
            buf.append(format("There are no obvious exits from here.%n"));
        } else {
            if (exits.size() == 1) {
                buf.append(format("There is a single exit to the %s%n", exits.get(0).getDescription()));
            } else {
                String exitsString = join(", ", exits.stream().map(Room.Exit::getDescription).collect(Collectors.toList()));
                buf.append(format("There are %d obvious exits: %s%n", exits.size(), exitsString));
            }
        }
        if (!items.isEmpty()) {
            buf.append(format("I can also see %s%n", join(", ", items.stream().map(Item::getDescription).collect(Collectors.toSet()))));
        }
        return buf.toString();
    });

    private static Result Inventory = new Inventory((items) -> {
        StringBuilder buf = new StringBuilder();
        if (items.isEmpty()) {
            buf.append(format("I ain't got nothing.%n"));
        }
        else {
            buf.append(format("I'm carrying %d items.%n %s%n", items.size(), join(System.getProperty("line.separator"), items.stream().map(Item::getDescription).collect(Collectors.toSet()))));
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