package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Room;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.Word;
import org.junit.Test;

import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RoomsTest {

    @Test
    public void roomsBuildsRoom() {
        Rooms roomSet = Rooms.newRoomSet();
        roomSet.newRoom().named("drop-in").describedAs("a padded cell").build();
        Set<Room> rooms = roomSet.copyOfRooms();

        assertFalse(rooms.isEmpty());
        assertEquals(1, rooms.size());

        Room dropIn = rooms.iterator().next();
        assertEquals(new Room("drop-in", "a padded cell"), dropIn);
        assertTrue(dropIn.getExits().isEmpty());
    }

    @Test
    public void roomsBuildsRoomWithExits() {
        Rooms roomSet = Rooms.newRoomSet();
        roomSet.newRoom()
                .named("stable")
                .describedAs("a horse stable")
                .withExit()
                .inDirectionOf("left")
                .towardsSelf()
                .buildExit()
                .build();
        Set<Room> rooms = roomSet.copyOfRooms();

        assertFalse(rooms.isEmpty());
        assertEquals(1, rooms.size());

        Room stable = rooms.iterator().next();
        assertFalse(stable.getExits().isEmpty());
        assertEquals(1, stable.getExits().size());
        assertTrue(stable.hasExit(new Word("left")));
        assertEquals(stable, stable.exit(new Word("LEFT")));
    }

    @Test
    public void roomsBuildsRoomsAndInterconnectingExits() {
        Rooms roomSet = Rooms.newRoomSet();
        roomSet.newRoom().named("forest").describedAs("a dense forest").withExit().inDirectionOf("east").towards("meadow").buildExit().build();
        roomSet.newRoom().named("meadow").describedAs("a peaceful meadow").withExit().inDirectionOf("west").towards("forest").buildExit().build();
        Set<Room> rooms = roomSet.copyOfRooms();

        assertEquals(2, rooms.size());

        Room forest = rooms.stream().filter(r -> r.getName().equals("forest")).findFirst().orElseThrow(AssertionError::new);
        Room meadow = rooms.stream().filter(r -> r.getName().equals("meadow")).findFirst().orElseThrow(AssertionError::new);
        assertTrue(forest.hasExit(new Word("east")));
        assertTrue(meadow.hasExit(new Word("west")));
        assertEquals(meadow, forest.exit(new Word("east")));
        assertEquals(forest, meadow.exit(new Word("west")));
    }

    @Test(expected = IllegalStateException.class)
    public void roomsThrowsExceptionGivenExitToUndefinedRoom() {
        Rooms roomSet = Rooms.newRoomSet();
        roomSet.newRoom().named("forest").describedAs("a dense forest").withExit().inDirectionOf("east").towards("dungeon").buildExit().build();
        roomSet.newRoom().named("meadow").describedAs("a peaceful meadow").withExit().inDirectionOf("west").towards("forest").buildExit().build();
        roomSet.copyOfRooms();
    }

    @Test
    public void roomsBuildsVocabularyOfDirectionWords() {
        Rooms roomSet = Rooms.newRoomSet();
        roomSet.newRoom().named("turbolift").describedAs("a dense forest")
                .withExit().inDirectionOf("deck1").towards("deck1").buildExit()
                .withExit().inDirectionOf("deck2").towards("deck2").buildExit()
                .withExit().inDirectionOf("deck3").towards("deck3").buildExit()
                .withExit().inDirectionOf("deck4").towards("deck4").buildExit()
                .build();
        roomSet.newRoom().named("deck1").describedAs("deck 1").withExit().inDirectionOf("turbolift").towards("turbolift").buildExit().build();
        roomSet.newRoom().named("deck2").describedAs("deck 2").withExit().inDirectionOf("turbolift").towards("turbolift").buildExit().build();
        roomSet.newRoom().named("deck3").describedAs("deck 3").withExit().inDirectionOf("turbolift").towards("turbolift").buildExit().build();
        roomSet.newRoom().named("deck4").describedAs("deck 4").withExit().inDirectionOf("turbolift").towards("turbolift").buildExit().build();
        Set<Room> rooms = roomSet.copyOfRooms();

        assertEquals(5, rooms.size());

        Vocabulary vocabulary = roomSet.buildVocabulary();

        assertTrue(vocabulary.findMatch("deck1").isPresent());
        assertTrue(vocabulary.findMatch("deck2").isPresent());
        assertTrue(vocabulary.findMatch("deck3").isPresent());
        assertTrue(vocabulary.findMatch("deck4").isPresent());
    }

    @Test
    public void roomsLastExitWithSameDirectionWins() {
        Rooms roomSet = Rooms.newRoomSet();
        roomSet.newRoom().named("turbolift").describedAs("a dense forest")
                .withExit().inDirectionOf("deck1").towards("deck1").buildExit()
                .withExit().inDirectionOf("deck1").towards("deck2").buildExit() // <--- wins
                .withExit().inDirectionOf("deck2").towards("deck3").buildExit()
                .withExit().inDirectionOf("deck2").towards("deck4").buildExit() // <--- wins
                .build();
        roomSet.newRoom().named("deck1").describedAs("deck 1").withExit().inDirectionOf("turbolift").towards("turbolift").buildExit().build();
        roomSet.newRoom().named("deck2").describedAs("deck 2").withExit().inDirectionOf("turbolift").towards("turbolift").buildExit().build();
        roomSet.newRoom().named("deck3").describedAs("deck 3").withExit().inDirectionOf("turbolift").towards("turbolift").buildExit().build();
        roomSet.newRoom().named("deck4").describedAs("deck 4").withExit().inDirectionOf("turbolift").towards("turbolift").buildExit().build();
        Set<Room> rooms = roomSet.copyOfRooms();

        assertEquals(5, rooms.size());

        Room turbolift = rooms.stream().filter(r -> r.getName().equals("turbolift")).findFirst().orElseThrow(AssertionError::new);
        Room deck2 = rooms.stream().filter(r -> r.getName().equals("deck2")).findFirst().orElseThrow(AssertionError::new);
        Room deck4 = rooms.stream().filter(r -> r.getName().equals("deck4")).findFirst().orElseThrow(AssertionError::new);
        assertEquals(deck2, turbolift.exit(new Word("deck1")));
        assertEquals(deck4, turbolift.exit(new Word("deck2")));
    }

}