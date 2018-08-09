package hartman.games.adventureland.adventures;

import hartman.games.adventureland.engine.Action;
import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Noun;
import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Verb;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.core.Actions;
import hartman.games.adventureland.engine.core.Nouns;
import hartman.games.adventureland.engine.core.Results;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static hartman.games.adventureland.engine.core.Actions.newActionSet;
import static hartman.games.adventureland.engine.core.Conditions.hasExit;
import static hartman.games.adventureland.engine.core.Conditions.isInRoom;
import static hartman.games.adventureland.engine.core.Conditions.isItemCarried;
import static hartman.games.adventureland.engine.core.Conditions.isItemHere;
import static hartman.games.adventureland.engine.core.Conditions.isPresent;
import static hartman.games.adventureland.engine.core.Conditions.not;
import static hartman.games.adventureland.engine.core.Conditions.random;
import static hartman.games.adventureland.engine.core.Conditions.times;
import static hartman.games.adventureland.engine.core.Nouns.DOOR;
import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Nouns.EAST;
import static hartman.games.adventureland.engine.core.Nouns.NORTH;
import static hartman.games.adventureland.engine.core.Nouns.SOUTH;
import static hartman.games.adventureland.engine.core.Nouns.UP;
import static hartman.games.adventureland.engine.core.Nouns.WEST;
import static hartman.games.adventureland.engine.core.Results.Drop;
import static hartman.games.adventureland.engine.core.Results.Get;
import static hartman.games.adventureland.engine.core.Results.Go;
import static hartman.games.adventureland.engine.core.Results.Quit;
import static hartman.games.adventureland.engine.core.Results.gotoRoom;
import static hartman.games.adventureland.engine.core.Results.printf;
import static hartman.games.adventureland.engine.core.Results.swap;
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

public class HouseEscapeAdventure {

    public static Adventure adventure() {

        /*
         * Vocabulary
         */

        Verb kill = new Verb("KILL", "SWAT", "HIT");
        Verb yell = new Verb("YELL", "SHOUT", "SCREAM");
        Noun fly = new Noun("FLY", "BUG", "INSECT", "PEST");


        /*
         *  ROOMS
         */

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


        /*
         * ITEMS
         */

        Item key = Item.newPortableObjectItem("key", "A brass key.", bedroom);
        Item lockedDoor = Item.newSceneryRoomItem("locked_door", "Locked door.", kitchen);
        Item openDoor = Item.newSceneryRoomItem("open_door", "An unlocked door.");
        Item flyswatter = Item.newPortableObjectItem("flyswatter", "A messy, green flyswatter", living_room);
        Set<Item> items = new LinkedHashSet<>(Arrays.asList(key, lockedDoor, openDoor, flyswatter));


        /*
         * ACTIONS
         */

        // Occurs

        Actions.ActionSet occurs = newActionSet();
        occurs.newAction().when(times(1)).then(Look).build();
        occurs.newAction().when(isInRoom(kitchen)).when(random(60)).then(printf("A fly buzzes past my ear.%n")).build();
        occurs.newAction().when(not(isInRoom(outside))).then(printf("What should I do? ")).build();
        occurs.newAction().when(isInRoom(outside)).then(printf("%n*** Congratulations, you've escaped! ***")).andThen(Quit).build();

        // Standard game actions

        Actions.ActionSet standardActions = newActionSet();
        standardActions.newAction().on(GO).withAnyNoun().when(hasExit).then(Go).andThen(Look).build();
        standardActions.newAction().on(LOOK).withAnyNoun().then(Look).build();
        standardActions.newAction().on(INVENTORY).withAnyNoun().then(Inventory).build();
        standardActions.newAction().on(QUIT).then(Quit).build();
        standardActions.newAction().onUnrecognizedVerb().then(printf("Sorry, I don't know how to do that.%n")).build();
        standardActions.newAction().onUnrecognizedVerb().withAnyNoun().then(printf("Sorry, I don't understand what you said.%n")).build();
        standardActions.newAction().onUnrecognizedVerb().withUnrecognizedNoun().then(printf("I don't know how to do that.%n")).build();
        standardActions.newAction().onAnyVerb().withUnrecognizedNoun().then(printf("I don't know what that is.%n")).build();
        standardActions.newAction().onAnyVerb().withNoNoun().then(printf("Do that with what?%n")).build();
        standardActions.newAction().onAnyVerb().withAnyNoun().then(printf("I don't know how to do that.")).build();

        // Adventure-specific actions

        Actions.ActionSet adventureActions = newActionSet();
        adventureActions.newAction().on(OPEN).with(DOOR).when(isItemHere(lockedDoor)).and(not(isPresent(key))).then(printf("%nIt's locked. I need some way to unlock it.%n")).build();
        adventureActions.newAction().on(GO).with(DOOR).when(isItemHere(lockedDoor)).then(printf("%nI can't. It's locked.%n")).build();
        adventureActions.newAction().on(GET).with(key).when(isItemHere(key)).then(Get).andThen(printf("%nOkay. I got the key. Type INVENTORY to see what I'm carrying.%n")).build();
        adventureActions.newAction().on(DROP).with(key).when(isItemCarried(key)).then(Drop).andThen(printf("%nI dropped the key.%n")).build();

        /*
         Same conditions and results, just different verbs and nouns so we can avoid duplicating and reuse the conditions and results portions of the Action Builder

         Instead of this:
            adventureActions.newAction().on(OPEN).with(DOOR).when(isItemHere(lockedDoor)).and(isPresent(key)).then(swap(lockedDoor, openDoor)).andThen(printf("<CLICK> That did it. It's unlocked.%n")).andThen(Look).build();
            adventureActions.newAction().on(USE).with(key).when(isItemHere(lockedDoor)).and(isPresent(key)).then(swap(lockedDoor, openDoor)).andThen(printf("<CLICK> That did it. It's unlocked.%n")).andThen(Look).build();

         */
        Action.Builder lockedDoorWithKey = adventureActions.newAction().when(isItemHere(lockedDoor)).and(isPresent(key)).then(swap(lockedDoor, openDoor)).andThen(printf("<CLICK> That did it. It's unlocked.%n")).andThen(Look);
        lockedDoorWithKey.on(OPEN).with(DOOR).build();
        lockedDoorWithKey.on(USE).with(key).build();

        adventureActions.newAction().on(GO).with(DOOR).when(isItemHere(openDoor)).then(gotoRoom(outside)).andThen(printf("%nYeah! I've made it outside!%n")).build();
        adventureActions.newAction().on(GET).with(flyswatter).when(isItemHere(flyswatter)).then(Get).andThen(printf("%nOkay. I picked up the flyswatter.%n")).build();
        adventureActions.newAction().on(kill).with(fly).when(isItemCarried(flyswatter)).then(printf("%nI got 'em. It's dead.%n")).build();
        adventureActions.newAction().on(yell).then(printf("%nYou don't have to yell. I can hear you.%n")).build();

        Actions.ActionSet fullActionSet = adventureActions.addAll(standardActions);


        /*
         * Build a vocabulary based off the verbs and nouns used in the Actions.
         */
        Vocabulary vocabulary = fullActionSet.buildVocabulary().merge(new Vocabulary(Collections.emptySet(), Nouns.directions()));

        return new Adventure(vocabulary, occurs.toSet(), fullActionSet.toSet(), items, hallway);
    }

    private static Action.Result Look = new Results.Look((room, exits, items) -> {
        StringBuilder buf = new StringBuilder();
        buf.append(format("%n%s%n", room.getDescription()));
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

    private static Action.Result Inventory = new Results.Inventory((items) -> {
        StringBuilder buf = new StringBuilder();
        if (items.isEmpty()) {
            buf.append(format("%nI ain't got nothing.%n"));
        } else {
            if (items.size() == 1) {
                buf.append(format("%nI'm carrying 1 item."));
            }
            else {
                buf.append(format("%nI'm carrying %d items.", items.size()));
            }
            buf.append(format("%n%s%n", join(System.getProperty("line.separator"), items.stream().map(Item::getDescription).collect(Collectors.toSet()))));
        }
        return buf.toString();
    });
}
