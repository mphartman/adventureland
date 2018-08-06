package hartman.games.adventureland.engine;

public interface GameElementVisitor {

    void visit(Item item);

    void visit(Room room);

    void visit(Room.Exit exit);
}
