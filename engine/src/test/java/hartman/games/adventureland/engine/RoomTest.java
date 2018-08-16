package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoomTest {

    @Test
    public void shouldReturnNextRoomGivenValidExit() {
        Word DOWN = new Word("DOWN", "D");
        Word UP = new Word("UP", "U");

        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");
        Room chamber = new Room("chamber", "A small, round chamber with stone walls and floor.");
        chamber.setExit(DOWN, dungeon);
        dungeon.setExit(UP, chamber);

        assertTrue(chamber.hasExit(DOWN));
        assertTrue(chamber.hasExit(new Word("DOWN")));
        assertTrue(chamber.hasExit(new Word("down")));
        assertTrue(chamber.hasExit(new Word("d")));
        assertTrue(chamber.hasExit(new Word("D")));

        assertEquals(dungeon, chamber.exit(DOWN));

        assertEquals(chamber, dungeon.exit(new Word("u")));
    }

    @Test
    public void shouldReturnSelfGivenExitWithOnlyNouns() {
        Word EAST = new Word("EAST", "e");

        Room kitchen = new Room("kitchen", "Good times a cookin'.");
        kitchen.setExitTowardsSelf(EAST);

        assertTrue(kitchen.hasExit(EAST));
        assertEquals(kitchen, kitchen.exit(EAST));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfExitDoesNotExist() {
        Word NORTH = new Word("NORTH", "n");
        Word SOUTH = new Word("SOUTH", "s");
        Word EAST = new Word("EAST", "e");

        Room hallway = new Room("hallway", "Long, well-lit hallway.");
        hallway.setExitTowardsSelf(NORTH);
        hallway.setExitTowardsSelf(SOUTH);

        hallway.exit(EAST);
    }

    @Test
    public void setExitShouldOverwriteGivenSameDirection() {
        Room hallway = new Room("hallway", "A long stone corridor.");
        Room kitchen = new Room("kitchen", "A huge kitchen.");
        Room diningRoom = new Room("diningRoom", "A dining room.");

        hallway.setExit(new Word("left"), diningRoom);
        hallway.setExit(new Word("right"), kitchen);
        hallway.setExit(new Word("left"), kitchen);

        assertEquals(kitchen, hallway.exit(new Word("left")));

    }
}
