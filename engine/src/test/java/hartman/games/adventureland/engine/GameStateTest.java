package hartman.games.adventureland.engine;

import hartman.games.adventureland.engine.core.Nouns;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class GameStateTest {

    @Test
    public void lookShouldPrintCurrentRoomDescription() throws UnsupportedEncodingException {
        Room bedroom = new Room("bedroom", "a comfortable room with a large four-poster bed.");
        bedroom.setExitTowardsSelf(Nouns.EAST);
        GameState gameState = new GameState(bedroom);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        gameState.look(new PrintStreamVisitor(ps));
        Assert.assertEquals("I'm in a comfortable room with a large four-poster bed."
                        + System.getProperty("line.separator")
                        + "There is a single visible exit EAST"
                        + System.getProperty("line.separator")
                        + System.getProperty("line.separator"),
                baos.toString("UTF-8"));
    }
}

class PrintStreamVisitor implements GameElementVisitor {
    private PrintStream out;

    public PrintStreamVisitor(PrintStream out) {
        this.out = out;
    }

    @Override
    public void visit(Item item) {
        out.println(item.getDescription());
    }

    @Override
    public void visit(Room room) {
        out.printf("I'm in %s%n", room.getDescription());
        int numberOfExits = room.numberOfExits();
        if (numberOfExits > 0) {
            if (numberOfExits == 1) {
                out.printf("There is a single visible exit ");
            } else {
                out.printf("There are %d visible exits:%n", numberOfExits);
            }
        } else {
            out.printf("There are no visible exits.%n");
        }
    }

    @Override
    public void visit(Room.Exit exit) {
        out.printf("%s%n%n", exit.getDescription());
    }
}