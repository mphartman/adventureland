package hartman.games.adventureland.api;

public interface GameService {

    Game startNewGame(Adventure adventure, String playerName);

    Turn takeTurn(Game game, String inputCommand);

}
