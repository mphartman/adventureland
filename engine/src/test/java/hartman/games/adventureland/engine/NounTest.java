package hartman.games.adventureland.engine;

import org.junit.Test;

import static org.junit.Assert.*;

public class NounTest {

    @Test
    public void equalsShouldReturnTrueGivenASynonym() {
        Noun north = new Noun("north", "n");
        assertTrue(north.equals(new Noun("north")));
        assertTrue(north.equals(new Noun("n")));
        assertTrue(north.equals(Noun.ANY));
        assertTrue(Noun.ANY.equals(north));
    }

}