package hartman.games.adventureland.engine;

public interface GameElement {
    void accept(GameElementVisitor visitor);
}
