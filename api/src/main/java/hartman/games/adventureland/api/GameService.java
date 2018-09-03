package hartman.games.adventureland.api;

public interface GameService {

    Turn takeTurn(Game game, String inputCommand);

}
