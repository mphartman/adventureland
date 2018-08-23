package hartman.games.adventureland.engine.core;

import hartman.games.adventureland.engine.Item;
import hartman.games.adventureland.engine.Room;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultDisplayTest {

    private DefaultDisplay display;
    private StringWriter out;

    @Before
    public void setupDisplay() {
        out = new StringWriter();
        display = new DefaultDisplay(new PrintWriter(out));
    }

    @Test
    public void printShouldOutputGivenMessage() {
        display.print("Hello, World");
        assertEquals("Hello, World", out.toString());
    }

    @Test
    public void lookShouldDescribeRoom() {
        display.look(Room.NOWHERE, Collections.emptyList());
        assertTrue(out.toString().contains(Room.NOWHERE.getDescription()));
    }

    @Test
    public void lookShouldDescribeRoomAndItem() {
        Room garage = new Room("garage", "A messy garage");
        Item wrench = new Item.Builder().named("wrench").describedAs("A monkey wrench").in(garage).build();

        display.look(garage, singletonList(wrench));
        assertTrue(out.toString().contains(garage.getDescription()));
        assertTrue(out.toString().contains(wrench.getDescription()));
    }

    @Test
    public void lookShouldDescribeRoomAndMultipleItems() {
        Room garage = new Room("garage", "A messy garage");
        Item wrench = new Item.Builder().named("wrench").describedAs("A monkey wrench").in(garage).build();
        Item hammer = new Item.Builder().named("hammer").describedAs("A hammer").in(garage).build();
        Item toolbox = new Item.Builder().named("toolbox").describedAs("A toolbox").in(garage).build();

        display.look(garage, asList(wrench, hammer, toolbox));
        assertTrue(out.toString().contains(garage.getDescription()));
        assertTrue(out.toString().contains(wrench.getDescription()));
        assertTrue(out.toString().contains(hammer.getDescription()));
        assertTrue(out.toString().contains(toolbox.getDescription()));
    }

    @Test
    public void inventoryShouldDescribeItems() {
        Item hammer = new Item.Builder().named("hammer").describedAs("A sledge hammer").build();
        Item nails = new Item.Builder().named("nails").describedAs("A pocket full of nails").build();

        display.inventory(emptyList());
        assertFalse(out.toString().contains(hammer.getDescription()));
        assertFalse(out.toString().contains(nails.getDescription()));

        display.inventory(singletonList(hammer));
        assertTrue(out.toString().contains(hammer.getDescription()));
        assertFalse(out.toString().contains(nails.getDescription()));

        display.inventory(asList(hammer, nails));
        assertTrue(out.toString().contains(hammer.getDescription()));
        assertTrue(out.toString().contains(nails.getDescription()));
    }
}