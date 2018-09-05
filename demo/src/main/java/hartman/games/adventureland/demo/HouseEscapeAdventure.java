package hartman.games.adventureland.demo;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.Display;
import hartman.games.adventureland.engine.Game;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.DefaultCommandInterpreter;
import hartman.games.adventureland.engine.core.DefaultDisplay;
import hartman.games.adventureland.engine.core.Items;

import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import static hartman.games.adventureland.engine.core.Actions.newActionSet;
import static hartman.games.adventureland.engine.core.Conditions.carrying;
import static hartman.games.adventureland.engine.core.Conditions.hasExitMatchingCommandWordAt;
import static hartman.games.adventureland.engine.core.Conditions.here;
import static hartman.games.adventureland.engine.core.Conditions.in;
import static hartman.games.adventureland.engine.core.Conditions.isFlagSet;
import static hartman.games.adventureland.engine.core.Conditions.not;
import static hartman.games.adventureland.engine.core.Conditions.present;
import static hartman.games.adventureland.engine.core.Conditions.random;
import static hartman.games.adventureland.engine.core.Results.destroy;
import static hartman.games.adventureland.engine.core.Results.drop;
import static hartman.games.adventureland.engine.core.Results.get;
import static hartman.games.adventureland.engine.core.Results.goInDirectionMatchingCommandWordAt;
import static hartman.games.adventureland.engine.core.Results.gotoRoom;
import static hartman.games.adventureland.engine.core.Results.incrementCounter;
import static hartman.games.adventureland.engine.core.Results.inventory;
import static hartman.games.adventureland.engine.core.Results.look;
import static hartman.games.adventureland.engine.core.Results.print;
import static hartman.games.adventureland.engine.core.Results.println;
import static hartman.games.adventureland.engine.core.Results.put;
import static hartman.games.adventureland.engine.core.Results.putHere;
import static hartman.games.adventureland.engine.core.Results.quit;
import static hartman.games.adventureland.engine.core.Results.setFlag;
import static hartman.games.adventureland.engine.core.Results.swap;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class HouseEscapeAdventure {

    public static void main(String[] args) {
        Adventure adventure = adventure();
        CommandInterpreter interpreter = new ConsoleInterpreter(adventure.getVocabulary());
        Display display = new ConsoleDisplay();
        GameState gameState = new GameState(adventure.getStartRoom(), adventure.getItems());
        Game game = new Game(adventure, interpreter, display, gameState);
        game.run();
    }

    private static class ConsoleInterpreter extends DefaultCommandInterpreter {
        private ConsoleInterpreter(Vocabulary vocabulary) {
            super(new Scanner(System.in), vocabulary);
        }
    }

    private static class ConsoleDisplay extends DefaultDisplay {
        private ConsoleDisplay() {
            super(new PrintWriter(System.out));
        }
    }

    private static Adventure adventure() {

        final String introduction = format(
                "House Escape, a text-based adventure.%n" +
                        "Copyright © 2018, Michael Hartman%n" +
                        "Distributed under the Apache License, version 2.%n%n" +
                        "A voice BOOMS out:%n" +
                        "\"In this adventure you're to escape from the house.\"%n%n" +
                        "Remember you can always say \"HELP\"%n"
        );

        /*
         * Vocabulary
         */

        Word HELP = new Word("HELP", "?");
        Word QUIT = new Word("QUIT");
        Word INVENTORY = new Word("INVENTORY", "I");
        Word LOOK = new Word("LOOK", "L");
        Word GO = new Word("GO", "GOTO", "ENTER", "WALK", "RUN", "EXIT", "LEAVE");
        Word OPEN = new Word("OPEN", "UNLOCK");
        Word GET = new Word("GET", "PICKUP", "GRAB", "TAKE");
        Word DROP = new Word("DROP", "DISCARD");
        Word USE = new Word("USE");
        Word NORTH = new Word("North", "N");
        Word SOUTH = new Word("South", "S");
        Word UP = new Word("Up", "U");
        Word DOWN = new Word("Down", "D");
        Word EAST = new Word("East", "E");
        Word WEST = new Word("West", "W");
        Word kill = new Word("Kill", "SWAT", "HURT", "HIT");
        Word yell = new Word("Yell", "SHOUT", "SCREAM");
        Word pet = new Word("PET", "PAT");
        Word door = new Word("Door");
        Word close = new Word("CLOSE", "SHUT");

        Set<Word> directionWords = new LinkedHashSet<>(asList(NORTH, SOUTH, UP, DOWN, EAST, WEST));
        Vocabulary movement = new Vocabulary(directionWords);

        /*
         *  ROOMS
         */

        Room hallway = new Room("hallway", format("I'm in a short, narrow hallway.%nThere's a carpeted flight of stairs going up.%nThe hallway continues to the south."));
        Room upperStairs = new Room("upper_stairs", "I'm on the top of the stairs.");
        Room masterBedroom = new Room("master_bedroom", "I'm in the master bedroom. There's a big king-size bed.");
        Room kitchen = new Room("kitchen", "I'm in the kitchen.");
        Room livingRoom = new Room("living_room", "I'm in a living room. There are some old couches.");
        Room outside = new Room("outside", "I'm outside the house.");
        Room isaacBedroom = new Room("isaac_bedroom", "I'm in what looks to be a little boy's room. It has posters of video games on the wall.");
        hallway.setExit(UP, upperStairs);
        hallway.setExit(SOUTH, kitchen);
        upperStairs.setExit(DOWN, hallway);
        upperStairs.setExit(SOUTH, masterBedroom);
        masterBedroom.setExit(NORTH, upperStairs);
        kitchen.setExit(NORTH, hallway);
        kitchen.setExit(WEST, livingRoom);
        livingRoom.setExit(EAST, kitchen);
        upperStairs.setExit(WEST, isaacBedroom);
        isaacBedroom.setExit(EAST, upperStairs);

        /*
         * ITEMS
         */

        Items itemSet = Items.newItemSet();
        Item key = itemSet.newItem().named("key").describedAs("a brass key").portable().in(isaacBedroom).build();
        Item lockedDoor = itemSet.newItem().named("locked_door").describedAs("a locked door").in(kitchen).build();
        Item openDoor = itemSet.newItem().named("open_door").describedAs("an unlocked door").build();
        Item flyswatter = itemSet.newItem().named("flyswatter").alias("swatter").describedAs("a green fly swatter").portable().in(livingRoom).build();
        Item redPanda = itemSet.newItem().named("panda").describedAs("a red panda stuffed animal").portable().in(isaacBedroom).build();
        Item fly = itemSet.newItem().named("fly").describedAs("a large house fly").build();
        Item deadFly = itemSet.newItem().named("deadFly").describedAs("a smeared stain that was once a fly").build();
        Item dog = itemSet.newItem().named("dog").describedAs("a cute little dachshund").build();
        Item kennelWithDog = itemSet.newItem().named("kennelWithDog").alias("kennel").describedAs("a small dog kennel filled with blankets with a closed door").in(livingRoom).build();
        Item emptyKennel = itemSet.newItem().named("emptyKennel").describedAs("an open dog kennel").build();
        Item openWindow = itemSet.newItem().named("window").describedAs("an open window").in(kitchen).build();
        Item closedWindow = itemSet.newItem().named("closed_window").describedAs("a closed window.").build();

        // *** Occurs - actions which all run automatically at the start of every turn *** /

        Actions occurs = newActionSet();

        occurs.newAction()
                .when(not(isFlagSet("init")))
                .then(println(introduction))
                .andThen(look)
                .andThen(setFlag("init", true))
                .build();

        // the fly
        occurs.newAction()
                .when(in(kitchen))
                .and(not(here(fly)))
                .and(here(openWindow))
                .and(random(50))
                .then(put(fly, kitchen))
                .andThen(println("A fly comes in through the open window and buzzes past my ear!"))
                .build();

        // Archie the dog
        occurs.newAction()
                .when(in(kitchen))
                .and(random(30))
                .then(println("I hear a faint whimper as if from a dog coming from the West."))
                .build();

        occurs.newAction()
                .when(here(kennelWithDog))
                .then(println("I hear a whimper from the kennel. Something looks to be under the blankets."))
                .build();

        occurs.newAction()
                .when(here(dog)).and(random(75))
                .then(println("The little dog starts licking you."))
                .build();

        // *** these occurs must come last *** /

        // user prompt
        occurs.newAction()
                .when(not(in(outside)))
                .then(print("> "))
                .build();

        // game end
        occurs.newAction()
                .when(in(outside))
                .then(println("*** Congratulations, you've escaped! ***"))
                .andThen(quit)
                .build();


        // *** Actions which are triggered in response to the player's input.                                   *** /
        // *** ORDER OF THE ACTIONS IS IMPORTANT                                                                *** /
        // *** First one whose Verb, Noun, and Conditions are true will run and then the others are skipped.    *** /

        Actions adventureActions = newActionSet();

        adventureActions.newAction()
                .on(OPEN).with(door)
                .when(here(lockedDoor))
                .and(not(present(key)))
                .then(println("It's locked. I need some way to unlock it."))
                .build();

        adventureActions.newAction()
                .on(GO).with(door)
                .when(here(lockedDoor))
                .then(println("I can't. It's locked. Perhaps try using the key?"))
                .build();

        adventureActions.newAction()
                .on(GET).with(key)
                .when(here(key))
                .then(get(key))
                .andThen(println("Okay. I got the key."))
                .andThen(inventory)
                .build();

        adventureActions.newAction()
                .on(DROP).with(key)
                .when(carrying(key))
                .then(drop(key))
                .andThen(println("I dropped the key."))
                .build();

        adventureActions.newAction()
                .on(OPEN).with(door)
                .when(here(lockedDoor))
                .and(present(key))
                .then(swap(lockedDoor, openDoor))
                .andThen(println("<CLICK> That did it. It's unlocked."))
                .andThen(look)
                .build();

        adventureActions.newAction()
                .on(USE).with(key)
                .when(here(lockedDoor))
                .and(present(key))
                .then(swap(lockedDoor, openDoor))
                .andThen(println("<CLICK> That did it. It's unlocked."))
                .andThen(look)
                .build();

        adventureActions.newAction()
                .on(GO).with(door)
                .when(here(openDoor))
                .then(gotoRoom(outside))
                .andThen(println("Yeah! I've made it outside!"))
                .build();

        adventureActions.newAction()
                .on(GET).with(flyswatter)
                .when(here(flyswatter))
                .then(get(flyswatter))
                .andThen(println("Okay. I picked up the flyswatter."))
                .andThen(inventory)
                .build();

        adventureActions.newAction()
                .on(kill).with(fly)
                .when(here(fly))
                .and(not(carrying(flyswatter)))
                .then(println("Smack! I tried but I'm not fast enough. I need some sort of tool."))
                .build();

        adventureActions.newAction()
                .on(kill).with(fly)
                .when(here(fly))
                .then(putHere(deadFly))
                .andThen(println("WHACK! I got 'em! It's dead."))
                .andThen(destroy(fly))
                .andThen(incrementCounter("deadFlies"))
                .build();

        adventureActions.newAction()
                .on(close).with(openWindow)
                .when(here(openWindow))
                .then(swap(openWindow, closedWindow))
                .andThen(println("It's closed. That should keep those pesky flies out of here."))
                .build();

        adventureActions.newAction()
                .on(OPEN).with(closedWindow)
                .when(here(closedWindow))
                .then(swap(closedWindow, openWindow))
                .andThen(println("It's open. A cool breeze greets me. I hear a buzzing sound coming from outside too."))
                .build();

        adventureActions.newAction()
                .on(yell).withAnySecondWord()
                .then(println("{word:2}!!! Now what?"))
                .build();

        adventureActions.newAction()
                .on(GET).with(redPanda)
                .when(here(redPanda))
                .then(get(redPanda))
                .andThen(println("Okay. I picked up the toy. It's a bit smelly but it's soft and I feel better carrying it."))
                .andThen(inventory)
                .build();

        adventureActions.newAction()
                .on(DROP).with(redPanda)
                .when(carrying(redPanda))
                .then(drop(redPanda))
                .andThen(look)
                .build();

        adventureActions.newAction()
                .on(OPEN).with(kennelWithDog)
                .when(here(kennelWithDog))
                .then(swap(kennelWithDog, emptyKennel))
                .andThen(putHere(dog))
                .andThen(println("A super cute little dog comes leaping out of the kennel!"))
                .build();

        adventureActions.newAction()
                .on(GET).with(dog)
                .when(here(dog))
                .then(println("He's a bit too excited and very fast. I can't catch him. Maybe when he calms down."))
                .build();

        adventureActions.newAction()
                .on(pet).with(dog)
                .when(here(dog))
                .then(println("The dog loves me. His leg starts thumping on the floor."))
                .build();


        // *** Standard game actions which apply to most games *** /

        Actions standardActions = newActionSet();

        // movement

        standardActions.newAction()
                .on(GO).withAnySecondWord()
                .when(hasExitMatchingCommandWordAt(2))
                .then(goInDirectionMatchingCommandWordAt(2))
                .andThen(look)
                .build();

        standardActions.newAction()
                .on(GO).withAnySecondWord()
                .then(println("{word:2} is not a valid exit from here."))
                .build();

        standardActions.newAction()
                .on(GO)
                .then(println("Go where?"))
                .build();

        standardActions.newAction()
                .onAnyFirstWords(directionWords.toArray(new Word[0]))
                .when(not(hasExitMatchingCommandWordAt(1)))
                .then(println("I can't go {word:1} from here. Try one of the obvious exits."))
                .build();

        standardActions.newAction()
                .onAnyFirstWord()
                .when(hasExitMatchingCommandWordAt(1))
                .then(goInDirectionMatchingCommandWordAt(1))
                .andThen(look)
                .build();

        standardActions.newAction()
                .on(LOOK)
                .then(look)
                .build();

        standardActions.newAction()
                .on(INVENTORY)
                .then(inventory)
                .build();

        standardActions.newAction()
                .on(QUIT)
                .then(quit)
                .build();

        standardActions.newAction()
                .on(HELP)
                .then(println("A voice BOOOMS out:\nTry --> \"GO, LOOK, JUMP, SWIM, CLIMB, TAKE, DROP\"\nand any other verbs you can think of..."))
                .build();

        // handle unrecognized input
        standardActions.newAction().onUnrecognizedFirstWord().withUnrecognizedSecondWord().then(println("Sorry, I don't know how to do that.")).build();
        standardActions.newAction().onUnrecognizedFirstWord().withAnySecondWord().then(println("Sorry, I don't know how to do that with a {word:2}.")).build();
        standardActions.newAction().onUnrecognizedFirstWord().then(println("Sorry, I don't recognize that word.")).build();
        standardActions.newAction().onAnyFirstWord().withUnrecognizedSecondWord().then(println("I don't know how to {word:1} that thing.")).build();
        standardActions.newAction().onAnyFirstWord().withAnySecondWord().then(println("I can't do that right now.")).build();
        standardActions.newAction().onAnyFirstWord().then(println("{word:1} what?")).build();

        /*
         * All the actions for this adventure
         */
        Actions fullActionSet = adventureActions.merge(standardActions);

        /*
         * Build a vocabulary based off the verbs and nouns used in the Actions.
         */
        Vocabulary vocabulary = fullActionSet.buildVocabulary().merge(movement);

        return new Adventure(vocabulary, occurs.copyOfActions(), fullActionSet.copyOfActions(), itemSet.copyOfItems(), masterBedroom);
    }

}