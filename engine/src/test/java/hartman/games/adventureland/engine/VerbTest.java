package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.*;

public class VerbTest {

    @Test
    public void synonymsShouldEquateToTrue() {
        Verb go = new Verb("go", "g", "run", "enter", "walk");
        assertTrue(go.equals(new Verb("go")));
        assertTrue(go.equals(new Verb("g")));
        assertTrue(go.equals(new Verb("run")));
        assertTrue(go.equals(new Verb("enter")));
        assertTrue(go.equals(new Verb("walk")));
        assertFalse(go.equals(new Verb("stroll")));
        assertTrue(Verb.ANY.equals(go));
        assertTrue(go.equals(Verb.ANY));
    }
}