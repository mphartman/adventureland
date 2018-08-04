package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoomTest {

    @Test
    public void shouldReturnNextRoomGivenValidExit() {
        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");

        Room.Exit downExit = new Room.Exit.Builder().exit(Direction.DOWN).towards(dungeon).build();

        Room chamber = new Room("chamber", "A small, round chamber with stone walls and floor.", downExit);

        assertTrue(chamber.hasExit(Direction.DOWN));
        assertEquals(dungeon, chamber.exit(Direction.DOWN));
    }

    @Test
    public void shouldReturnSelfGivenExitWithOnlyDirection() {
        Room kitchen = new Room("kitchen", "Good times a cookin'.", new Room.Exit.Builder().exit(Direction.EAST).towardsSelf().build());

        assertTrue(kitchen.hasExit(Direction.EAST));
        assertEquals(kitchen, kitchen.exit(Direction.EAST));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfExitDoesNotExist() {
        Room.Exit.Builder builder = new Room.Exit.Builder();
        Room hallway = new Room("hallway", "Long, well-lit hallway.", builder.exit(Direction.NORTH).towardsSelf().build(), builder.exit(Direction.SOUTH).towardsSelf().build());

        hallway.exit(Direction.EAST);
    }
}
