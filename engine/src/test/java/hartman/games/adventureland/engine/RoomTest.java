package hartman.games.adventureland.engine;

import org.junit.Test;

import static hartman.games.adventureland.engine.core.Nouns.DOWN;
import static hartman.games.adventureland.engine.core.Nouns.EAST;
import static hartman.games.adventureland.engine.core.Nouns.NORTH;
import static hartman.games.adventureland.engine.core.Nouns.SOUTH;
import static hartman.games.adventureland.engine.core.Nouns.UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoomTest {

    @Test
    public void shouldReturnNextRoomGivenValidExit() {
        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");
        Room chamber = new Room("chamber", "A small, round chamber with stone walls and floor.");
        chamber.setExit(DOWN, dungeon);
        dungeon.setExit(UP, chamber);

        assertTrue(chamber.hasExit(DOWN));
        assertTrue(chamber.hasExit(new Noun("DOWN")));
        assertTrue(chamber.hasExit(new Noun("down")));
        assertTrue(chamber.hasExit(new Noun("d")));
        assertTrue(chamber.hasExit(new Noun("D")));

        assertEquals(dungeon, chamber.exit(DOWN));

        assertEquals(chamber, dungeon.exit(new Noun("u")));
    }

    @Test
    public void shouldReturnSelfGivenExitWithOnlyNouns() {
        Room kitchen = new Room("kitchen", "Good times a cookin'.");
        kitchen.setExitTowardsSelf(EAST);

        assertTrue(kitchen.hasExit(EAST));
        assertEquals(kitchen, kitchen.exit(EAST));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfExitDoesNotExist() {
        Room hallway = new Room("hallway", "Long, well-lit hallway.");
        hallway.setExitTowardsSelf(NORTH);
        hallway.setExitTowardsSelf(SOUTH);

        hallway.exit(EAST);
    }
}
