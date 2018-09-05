package hartman.games.adventureland.api;

import hartman.games.adventureland.engine.Adventure;
import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.core.DefaultCommandInterpreter;
import hartman.games.adventureland.engine.core.DefaultDisplay;
import hartman.games.adventureland.script.AdventureScriptParser;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;
import java.util.Scanner;

@Service
public class GameServiceImpl implements GameService {

    private final AdventureScriptParser parser;
    private final TurnRepository turnRepository;
    private final GameRepository gameRepository;

    @Autowired
    public GameServiceImpl(AdventureScriptParser parser, TurnRepository turnRepository, GameRepository gameRepository) {
        this.parser = parser;
        this.turnRepository = turnRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional
    public Turn takeTurn(Game game, String inputCommand) {
        return getAdventure(game)
                .map(adventure -> takeTurnInGame(adventure, game, inputCommand))
                .map(tuple -> {
                    gameRepository.save(game.update(tuple.getGameState()));
                    return turnRepository.save(tuple.getTurn());
                })
                .orElseThrow(IllegalStateException::new);
    }

    private Optional<Adventure> getAdventure(Game game) {
        return Optional.ofNullable(game.getAdventure().getScript())
                .map(AdventureScript::getScript)
                .map(StringReader::new)
                .map(this::parse);
    }

    private GameStateTurnTuple takeTurnInGame(Adventure adventure, Game game, String inputCommand) {
        CommandInterpreter interpreter = new StringCommandInterpreter(inputCommand, adventure.getVocabulary());
        StringWriter displayOut = new StringWriter();
        DefaultDisplay display = new DefaultDisplay(new PrintWriter(displayOut));
        GameState gameState = game.load().orElse(new GameState(adventure.getStartRoom(), adventure.getItems()));
        hartman.games.adventureland.engine.Game engineGame = new hartman.games.adventureland.engine.Game(adventure, interpreter, display, gameState);
        gameState = engineGame.takeTurn(interpreter.nextCommand());
        Turn turn = new Turn(game, inputCommand, displayOut.toString());
        return new GameStateTurnTuple(gameState, turn);
    }

    private Adventure parse(Reader reader) {
        try {
            return parser.parse(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private class StringCommandInterpreter extends DefaultCommandInterpreter {
        private StringCommandInterpreter(String command, Vocabulary vocabulary) {
            super(new Scanner(command), vocabulary);
        }
    }

    @Value
    private static class GameStateTurnTuple {
        private final GameState gameState;
        private final Turn turn;
    }

}
