package hartman.games.adventureland.engine;

import org.junit.Test;

import hartman.games.adventureland.engine.core.Nouns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoomTest {

    @Test
    public void shouldReturnNextRoomGivenValidExit() {
        Room dungeon = new Room("dungeon", "A dimly lit, cold space. It smells.");

        Room.Exit downExit = new Room.Exit.Builder().exit(Nouns.DOWN).towards(dungeon).build();

        Room chamber = new Room("chamber", "A small, round chamber with stone walls and floor.", downExit);

        assertTrue(chamber.hasExit(Nouns.DOWN));
        assertEquals(dungeon, chamber.exit(Nouns.DOWN));
    }

    @Test
    public void shouldReturnSelfGivenExitWithOnlyNouns() {
        Room kitchen = new Room("kitchen", "Good times a cookin'.", new Room.Exit.Builder().exit(Nouns.EAST).towardsSelf().build());

        assertTrue(kitchen.hasExit(Nouns.EAST));
        assertEquals(kitchen, kitchen.exit(Nouns.EAST));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfExitDoesNotExist() {
        Room.Exit.Builder builder = new Room.Exit.Builder();
        Room hallway = new Room("hallway", "Long, well-lit hallway.", builder.exit(Nouns.NORTH).towardsSelf().build(), builder.exit(Nouns.SOUTH).towardsSelf().build());

        hallway.exit(Nouns.EAST);
    }
}
