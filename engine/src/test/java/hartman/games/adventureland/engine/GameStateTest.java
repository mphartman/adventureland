package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Nouns;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class GameStateTest {

    @Test
    public void exitTowardsShouldUpdateCurrentRoomGivenValidExit() {
        Room start = new Room("start", "I am here.");
        Room end = new Room("end", "I am here now.");
        start.setExit(new Noun("LEFT"), end);

        GameState gameState = new GameState(start);
        Room former = gameState.exitTowards(new Noun("LEFT"));

        Assert.assertEquals(former, start);
        Assert.assertEquals(end, gameState.getCurrentRoom());
    }
}
