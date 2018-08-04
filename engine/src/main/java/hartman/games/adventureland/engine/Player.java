package hartman.games.adventureland.engine;

/**
 * Avatar of the person playing the game.
 */
public class Player {
    private final String name;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
