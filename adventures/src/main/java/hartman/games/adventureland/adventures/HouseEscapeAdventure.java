package hartman.games.adventureland.adventures;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Items;
import hartman.games.adventureland.engine.core.Results;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hartman.games.adventureland.engine.core.Actions.newActionSet;
import static hartman.games.adventureland.engine.core.Conditions.currentRoomHasExit;
import static hartman.games.adventureland.engine.core.Conditions.currentRoomHasExitByVerb;
import static hartman.games.adventureland.engine.core.Conditions.isInRoom;
import static hartman.games.adventureland.engine.core.Conditions.isItemCarried;
import static hartman.games.adventureland.engine.core.Conditions.isItemHere;
import static hartman.games.adventureland.engine.core.Conditions.isPresent;
import static hartman.games.adventureland.engine.core.Conditions.not;
import static hartman.games.adventureland.engine.core.Conditions.random;
import static hartman.games.adventureland.engine.core.Conditions.times;
import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Nouns.EAST;
import static hartman.games.adventureland.engine.core.Nouns.NORTH;
import static hartman.games.adventureland.engine.core.Nouns.SOUTH;
import static hartman.games.adventureland.engine.core.Nouns.UP;
import static hartman.games.adventureland.engine.core.Nouns.WEST;
import static hartman.games.adventureland.engine.core.Results.drop;
import static hartman.games.adventureland.engine.core.Results.get;
import static hartman.games.adventureland.engine.core.Results.go;
import static hartman.games.adventureland.engine.core.Results.goUsingVerb;
import static hartman.games.adventureland.engine.core.Results.gotoRoom;
import static hartman.games.adventureland.engine.core.Results.printf;
import static hartman.games.adventureland.engine.core.Results.println;
import static hartman.games.adventureland.engine.core.Results.put;
import static hartman.games.adventureland.engine.core.Results.quit;
import static hartman.games.adventureland.engine.core.Results.swap;
import static hartman.games.adventureland.engine.core.Verbs.DROP;
import static hartman.games.adventureland.engine.core.Verbs.GET;
import static hartman.games.adventureland.engine.core.Verbs.GO;
import static hartman.games.adventureland.engine.core.Verbs.GO_DOWN;
import static hartman.games.adventureland.engine.core.Verbs.GO_EAST;
import static hartman.games.adventureland.engine.core.Verbs.GO_NORTH;
import static hartman.games.adventureland.engine.core.Verbs.GO_SOUTH;
import static hartman.games.adventureland.engine.core.Verbs.GO_UP;
import static hartman.games.adventureland.engine.core.Verbs.GO_WEST;
import static hartman.games.adventureland.engine.core.Verbs.HELP;
import static hartman.games.adventureland.engine.core.Verbs.INVENTORY;
import static hartman.games.adventureland.engine.core.Verbs.LOOK;
import static hartman.games.adventureland.engine.core.Verbs.OPEN;
import static hartman.games.adventureland.engine.core.Verbs.QUIT;
import static hartman.games.adventureland.engine.core.Verbs.USE;
import static java.lang.String.format;
import static java.lang.String.join;

public class HouseEscapeAdventure {

    public static Adventure adventure() {

        /*
         * Vocabulary
         */

        Verb kill = new Verb("KILL", "SWAT", "HIT");
        Verb yell = new Verb("YELL", "SHOUT", "SCREAM");
        Noun DOOR = new Noun("Door");

        Set<Noun> directionNouns = new LinkedHashSet<>(Arrays.asList(NORTH, SOUTH, UP, DOWN, EAST, WEST));
        Set<Verb> directionVerbs = new LinkedHashSet<>(Arrays.asList(GO_NORTH, GO_SOUTH, GO_UP, GO_DOWN, GO_EAST, GO_WEST));
        Vocabulary movement = new Vocabulary(directionVerbs, directionNouns);

        /*
         *  ROOMS
         */

        Room hallway = new Room("hallway", format("I'm in a short, narrow hallway.%nThere's a carpeted flight of stairs going up.%nThe hallway continues to the south."));
        Room upperStairs = new Room("upper_stairs", "I'm on the top of the stairs.");
        Room masterBedroom = new Room("master_bedroom", "I'm in the master bedroom.");
        Room kitchen = new Room("kitchen", "I'm in the kitchen.");
        Room livingRoom = new Room("living_room", "I'm in a living room with old couches.");
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

        Items.ItemSet itemSet = Items.newItemSet();
        Item key = itemSet.newItem().named("key").describedAs("a brass key").portable().in(masterBedroom).build();
        Item lockedDoor = itemSet.newItem().named("locked_door").describedAs("a locked door").in(kitchen).build();
        Item openDoor = itemSet.newItem().named("open_door").describedAs("an unlocked door").build();
        Item flyswatter = itemSet.newItem().named("flyswatter").alias("swatter").describedAs("a green fly swatter").portable().in(livingRoom).build();
        Item redPanda = itemSet.newItem().named("panda").describedAs("a red panda stuffed animal").portable().in(isaacBedroom).build();
        Item fly = itemSet.newItem().named("fly").describedAs("a large house fly").build();
        Item deadFly = itemSet.newItem().named("deadFly").describedAs("a smeared stain that was once a fly").build();

        /*
         * ACTIONS
         */

        // Occurs

        Actions.ActionSet occurs = newActionSet();
        occurs.newAction().when(times(1)).then(look).build();
        occurs.newAction().when(isInRoom(kitchen)).and(not(isItemHere(fly))).and(random(60)).then(put(fly, kitchen)).andThen(printf("%nA fly buzzes past my ear!%n")).build();
        occurs.newAction().when(not(isInRoom(outside))).then(printf("%nWhat should I do? ")).build();
        occurs.newAction().when(isInRoom(outside)).then(printf("%n*** Congratulations, you've escaped! ***")).andThen(quit).build();

        // Adventure-specific actions

        Actions.ActionSet adventureActions = newActionSet();

        adventureActions.newAction().on(OPEN).with(DOOR).when(isItemHere(lockedDoor)).and(not(isPresent(key))).then(printf("%nIt's locked. I need some way to unlock it.%n")).build();
        adventureActions.newAction().on(GO).with(DOOR).when(isItemHere(lockedDoor)).then(printf("%nI can't. It's locked.%n")).build();
        adventureActions.newAction().on(GET).with(key).when(isItemHere(key)).then(get).andThen(printf("%nOkay. I got the key. Type INVENTORY to see what I'm carrying.%n")).build();
        adventureActions.newAction().on(DROP).with(key).when(isItemCarried(key)).then(drop).andThen(printf("%nI dropped the key.%n")).build();
        Action.Builder lockedDoorWithKey = adventureActions.newAction().when(isItemHere(lockedDoor)).and(isPresent(key)).then(swap(lockedDoor, openDoor)).andThen(printf("<CLICK> That did it. It's unlocked.%n")).andThen(look);
        lockedDoorWithKey.on(OPEN).with(DOOR).build();
        lockedDoorWithKey.on(USE).with(key).build();

        adventureActions.newAction().on(GO).with(DOOR).when(isItemHere(openDoor)).then(gotoRoom(outside)).andThen(printf("%nYeah! I've made it outside!%n")).build();
        adventureActions.newAction().on(GET).with(flyswatter).when(isItemHere(flyswatter)).then(get).andThen(printf("%nOkay. I picked up the flyswatter.%n")).build();
        adventureActions.newAction().on(kill).with(fly).when(isItemHere(fly)).and(not(isItemCarried(flyswatter))).then(printf("%nSmack! I tried but I'm not fast enough. I need some sort of tool.%n")).build();
        adventureActions.newAction().on(kill).with(fly).when(isItemHere(fly)).then(swap(fly, deadFly)).andThen(printf("%nWHACK! I got 'em! It's dead.%n")).build();

        adventureActions.newAction().on(yell).withAnyNoun().then(printf("%nYou don't have to yell. I can hear you.%n")).build();

        adventureActions.newAction().on(GET).with(redPanda).when(isItemHere(redPanda)).then(get).andThen(println("%nOkay. I picked up the toy. It's a bit smelly but it's soft and I feel better carrying it.")).andThen(inventory).build();
        adventureActions.newAction().on(DROP).with(redPanda).when(isItemCarried(redPanda)).then(drop).andThen(look).build();

        // *** Standard game actions ***

        Actions.ActionSet standardActions = newActionSet();

        // movement
        Action.Condition isDirectionVerb = (command, gameState) -> directionVerbs.contains(command.getVerb());
        Action.Builder moveByVerb = standardActions.newAction().onAnyVerb().withNoNoun();
        moveByVerb.when(isDirectionVerb).and(currentRoomHasExitByVerb).then(goUsingVerb).andThen(look).build();
        moveByVerb.when(isDirectionVerb).and(not(currentRoomHasExitByVerb)).then(println("That's not an exit from here. Try one of the obvious exits.")).build();
        standardActions.newAction().on(GO).withNoNoun().then(println("{verb} where?")).build();
        standardActions.newAction().on(GO).withUnrecognizedNoun().then(println("I can't go that direction. Try one of the obvious exits.")).build();
        standardActions.newAction().on(GO).withAnyNoun().when(currentRoomHasExit).then(go).andThen(look).build();
        standardActions.newAction().on(GO).withAnyNoun().then(println("That's not an exit from here. Try one of the obvious exits.")).build();

        standardActions.newAction().on(LOOK).withAnyNoun().then(look).build();
        standardActions.newAction().on(INVENTORY).withAnyNoun().then(inventory).build();
        standardActions.newAction().on(QUIT).then(quit).build();
        standardActions.newAction().on(HELP).then(println("A voice BOOOMS out:\nTry --> \"GO, LOOK, JUMP, SWIM, CLIMB, TAKE, DROP\"\nand any other verbs you can think of...")).build();

        // unrecognized input
        standardActions.newAction().onUnrecognizedVerb().then(println("Sorry, I don't know how to do that.")).build();
        standardActions.newAction().onUnrecognizedVerb().withUnrecognizedNoun().then(println("Sorry, I don't know how to do that with that thing.")).build();
        standardActions.newAction().onUnrecognizedVerb().withAnyNoun().then(println("Sorry, I don't know how to that with a {noun}.")).build();
        standardActions.newAction().onAnyVerb().withUnrecognizedNoun().then(println("I don't know how to {verb} with that thing.")).build();
        standardActions.newAction().onAnyVerb().withNoNoun().then(println("{verb} what?")).build();
        standardActions.newAction().onAnyVerb().withAnyNoun().then(println("I can't do that here right now.")).build();

        Actions.ActionSet fullActionSet = adventureActions.addAll(standardActions);

        /*
         * Build a vocabulary based off the verbs and nouns used in the Actions.
         */
        Vocabulary vocabulary = Vocabulary.merge(fullActionSet.buildVocabulary(), movement);

        return new Adventure(vocabulary, occurs.copyOfActions(), fullActionSet.copyOfActions(), itemSet.copyOfItems(), hallway);
    }

    private static Action.Result look = Results.look((room, exits, items) -> {
        StringBuilder buf = new StringBuilder();
        buf.append(format("%n%s%n", room.getDescription()));
        if (!items.isEmpty()) {
            if (items.size() == 1) {
                buf.append(format("I can also see %s%n", items.get(0).getDescription()));
            }
            else {
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

    private static Action.Result inventory = Results.inventory((items) -> {
        StringBuilder buf = new StringBuilder();
        if (items.isEmpty()) {
            buf.append(format("%nI'm not carrying anything right now.%n"));
        } else {
            buf.append(format("%nI'm carrying "));
            if (items.size() == 1) {
                buf.append(format("%s%n", items.get(0).getDescription()));
            }
            else {
                buf.append(format("%d items: ", items.size()));
                IntStream.range(0, items.size()).forEachOrdered(i -> {
                    buf.append(format("%n - %s", items.get(i).getDescription()));
                });
            }
            buf.append('\n');
        }
        return buf.toString();
    });
}
