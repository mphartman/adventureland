package hartman.games.adventureland.adventures;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Items;
import hartman.games.adventureland.engine.core.Results;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hartman.games.adventureland.engine.Action.Result;
import static hartman.games.adventureland.engine.core.Actions.newActionSet;
import static hartman.games.adventureland.engine.core.Conditions.carrying;
import static hartman.games.adventureland.engine.core.Conditions.here;
import static hartman.games.adventureland.engine.core.Conditions.in;
import static hartman.games.adventureland.engine.core.Conditions.not;
import static hartman.games.adventureland.engine.core.Conditions.present;
import static hartman.games.adventureland.engine.core.Conditions.random;
import static hartman.games.adventureland.engine.core.Conditions.roomHasExit;
import static hartman.games.adventureland.engine.core.Conditions.times;
import static hartman.games.adventureland.engine.core.Results.drop;
import static hartman.games.adventureland.engine.core.Results.get;
import static hartman.games.adventureland.engine.core.Results.go;
import static hartman.games.adventureland.engine.core.Results.gotoRoom;
import static hartman.games.adventureland.engine.core.Results.printf;
import static hartman.games.adventureland.engine.core.Results.println;
import static hartman.games.adventureland.engine.core.Results.put;
import static hartman.games.adventureland.engine.core.Results.putHere;
import static hartman.games.adventureland.engine.core.Results.quit;
import static hartman.games.adventureland.engine.core.Results.swap;
import static hartman.games.adventureland.engine.core.Words.DOWN;
import static hartman.games.adventureland.engine.core.Words.DROP;
import static hartman.games.adventureland.engine.core.Words.EAST;
import static hartman.games.adventureland.engine.core.Words.GET;
import static hartman.games.adventureland.engine.core.Words.GO;
import static hartman.games.adventureland.engine.core.Words.HELP;
import static hartman.games.adventureland.engine.core.Words.INVENTORY;
import static hartman.games.adventureland.engine.core.Words.LOOK;
import static hartman.games.adventureland.engine.core.Words.NORTH;
import static hartman.games.adventureland.engine.core.Words.OPEN;
import static hartman.games.adventureland.engine.core.Words.QUIT;
import static hartman.games.adventureland.engine.core.Words.SOUTH;
import static hartman.games.adventureland.engine.core.Words.UP;
import static hartman.games.adventureland.engine.core.Words.USE;
import static hartman.games.adventureland.engine.core.Words.WEST;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;

public class HouseEscapeAdventure {

    public static Adventure adventure() {

        /*
         * Vocabulary
         */

        Word kill = new Word("KILL", "SWAT", "HURT");
        Word yell = new Word("YELL", "SHOUT", "SCREAM");
        Word pet = new Word("PET", "PAT");
        Word door = new Word("Door");

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


        // *** Occurs - actions which all run automatically at the start of every turn *** /

        Actions occurs = newActionSet();

        // `look` on game startup
        occurs.newAction()
                .when(times(1))
                .then(look)
                .build();

        // the fly
        occurs.newAction()
                .when(in(kitchen)).and(not(here(fly))).and(random(50))
                .then(put(fly, kitchen)).andThen(printf("%nA fly buzzes past my ear!%n"))
                .build();

        // Archie the dog
        occurs.newAction()
                .when(in(kitchen)).and(random(30))
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
                .then(printf("%nWhat should I do? "))
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
                .on(OPEN).the(door)
                .when(here(lockedDoor)).and(not(present(key)))
                .then(printf("%nIt's locked. I need some way to unlock it.%n"))
                .build();

        adventureActions.newAction()
                .on(GO).with(door)
                .when(here(lockedDoor))
                .then(printf("%nI can't. It's locked. Perhaps try using the key?%n"))
                .build();

        adventureActions.newAction()
                .on(GET).the(key)
                .when(here(key))
                .then(get).andThen(printf("%nOkay. I got the key.%n"))
                .build();

        adventureActions.newAction()
                .on(DROP).the(key)
                .when(carrying(key))
                .then(drop).andThen(printf("%nI dropped the key.%n"))
                .build();

        adventureActions.newAction()
                .on(OPEN).the(door)
                .when(here(lockedDoor)).and(present(key))
                .then(swap(lockedDoor, openDoor)).andThen(printf("<CLICK> That did it. It's unlocked.%n")).andThen(look)
                .build();

        adventureActions.newAction()
                .on(USE).the(key)
                .when(here(lockedDoor)).and(present(key))
                .then(swap(lockedDoor, openDoor)).andThen(printf("<CLICK> That did it. It's unlocked.%n")).andThen(look)
                .build();

        adventureActions.newAction()
                .on(GO).with(door)
                .when(here(openDoor))
                .then(gotoRoom(outside)).andThen(printf("%nYeah! I've made it outside!%n"))
                .build();

        adventureActions.newAction()
                .on(GET).the(flyswatter)
                .when(here(flyswatter))
                .then(get).andThen(printf("%nOkay. I picked up the flyswatter.%n"))
                .build();

        adventureActions.newAction()
                .on(kill).the(fly)
                .when(here(fly)).and(not(carrying(flyswatter)))
                .then(printf("%nSmack! I tried but I'm not fast enough. I need some sort of tool.%n"))
                .build();

        adventureActions.newAction()
                .on(kill).the(fly)
                .when(here(fly))
                .then(swap(fly, deadFly)).andThen(printf("%nWHACK! I got 'em! It's dead.%n"))
                .build();

        adventureActions.newAction()
                .on(yell).anything()
                .then(printf("%n\"{noun}\"!!! Now what?%n"))
                .build();

        adventureActions.newAction()
                .on(GET).the(redPanda)
                .when(here(redPanda))
                .then(get).andThen(println("%nOkay. I picked up the toy. It's a bit smelly but it's soft and I feel better carrying it.")).andThen(inventory)
                .build();

        adventureActions.newAction()
                .on(DROP).the(redPanda)
                .when(carrying(redPanda))
                .then(drop).andThen(look)
                .build();

        adventureActions.newAction()
                .on(OPEN).the(kennelWithDog)
                .when(here(kennelWithDog))
                .then(swap(kennelWithDog, emptyKennel)).andThen(putHere(dog)).andThen(println("A super cute little dog comes leaping out of the kennel!"))
                .build();

        adventureActions.newAction()
                .on(GET).the(dog)
                .when(here(dog))
                .then(println("He's a bit too excited and very fast. I can't catch him. Maybe when he calms down."))
                .build();

        adventureActions.newAction()
                .on(pet).the(dog)
                .when(here(dog))
                .then(println("The dog loves me. His leg starts thumping on the floor."))
                .build();


        // *** Standard game actions which apply to most games *** /

        Actions standardActions = newActionSet();

        // movement

        standardActions.newAction()
                .on(GO).withNoSecondWord()
                .then(println("Where do you want me to go?"))
                .build();

        standardActions.newAction()
                .on(GO)
                .when(roomHasExit)
                .then(go).andThen(look)
                .build();

        standardActions.newAction()
                .on(GO)
                .then(println("I can't go that way. Try one of the obvious exits."))
                .build();

        standardActions.newAction()
                .onAnyFirstWord().withNoSecondWord()
                .when(roomHasExit)
                .then(go).andThen(look)
                .build();

        standardActions.newAction()
                .on(LOOK)
                .then(look)
                .build();

        standardActions.newAction()
                .on(LOOK)
                .withAnySecondWord()
                .then(look)
                .build();

        standardActions.newAction()
                .on(INVENTORY)
                .then(inventory)
                .build();

        standardActions.newAction()
                .on(INVENTORY)
                .withAnySecondWord()
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
        standardActions.newAction().onUnrecognizedFirstWord().then(println("Sorry, I don't know how to do that.")).build();
        standardActions.newAction().onUnrecognizedFirstWord().withUnrecognizedSecondWord().then(println("Sorry, I don't know how to do that with that thing.")).build();
        standardActions.newAction().onUnrecognizedFirstWord().withAnySecondWord().then(println("Sorry, I don't know how to that with a {noun}.")).build();
        standardActions.newAction().onAnyFirstWord().withUnrecognizedSecondWord().then(println("I don't know how to {verb} with that thing.")).build();
        standardActions.newAction().onAnyFirstWord().withNoSecondWord().then(println("{verb} what?")).build();
        standardActions.newAction().onAnyFirstWord().withAnySecondWord().then(println("I can't do that here right now.")).build();

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

    private static Result look = Results.look((room, exits, items) -> {
        StringBuilder buf = new StringBuilder();
        buf.append(format("%n%s%n", room.getDescription()));
        if (!items.isEmpty()) {
            if (items.size() == 1) {
                buf.append(format("I can also see %s%n", items.get(0).getDescription()));
            } else {
                buf.append(format("I can also see %d other things here: ", items.size()));
                IntStream.range(0, items.size()).forEachOrdered(i -> {
                    if (i > 0) {
                        buf.append(", ");
                    }
                    if (i == (items.size() - 1)) {
                        buf.append("and ");
                    }
                    buf.append(items.get(i).getDescription());
                });
                buf.append('\n');
            }
        }
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
        return buf.toString();
    });

    private static Result inventory = Results.inventory((items) -> {
        StringBuilder buf = new StringBuilder();
        if (items.isEmpty()) {
            buf.append(format("%nI'm not carrying anything right now.%n"));
        } else {
            buf.append(format("%nI'm carrying "));
            if (items.size() == 1) {
                buf.append(format("%s%n", items.get(0).getDescription()));
            } else {
                buf.append(format("%d items: ", items.size()));
                IntStream.range(0, items.size()).forEachOrdered(i -> buf.append(format("%n - %s", items.get(i).getDescription())));
            }
            buf.append('\n');
        }
        return buf.toString();
    });
}
