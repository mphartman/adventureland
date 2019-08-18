package hartman.games.adventureland.api;

import hartman.games.adventureland.engine.CommandInterpreter;
import hartman.games.adventureland.engine.GameState;
import hartman.games.adventureland.engine.Vocabulary;
import hartman.games.adventureland.engine.core.DefaultCommandInterpreter;
import hartman.games.adventureland.engine.core.DefaultDisplay;
import hartman.games.adventureland.script.AdventureScriptParser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GameService {

    AdventureScriptParser parser;
    TurnRepository turnRepository;
    GameRepository gameRepository;

    @Transactional
    public Game startNewGame(hartman.games.adventureland.api.Adventure adventure, String playerName) {
        Game game = Game
                .builder()
                .adventure(adventure)
                .player(playerName)
                .status(Game.Status.READY)
                .startTime(LocalDateTime.now())
                .build();
        game = gameRepository.save(game);

        // take initial "start" turn automatically so any introductory OCCURS can run
        takeTurn(game, "");

        return game;
    }

    @Transactional
    public Turn takeTurn(Game game, String inputCommand) {
        return getAdventure(game)
                .map(adventure -> takeTurnInGame(adventure, game, inputCommand))
                .map(tuple -> {
                    GameState gameState = tuple.getGameState();
                    game.setStatus(gameState.isRunning() ? Game.Status.RUNNING : Game.Status.GAME_OVER);
                    game.update(gameState);
                    gameRepository.save(game);
                    return turnRepository.save(tuple.getTurn());
                })
                .orElseThrow(IllegalStateException::new);
    }

    private Optional<hartman.games.adventureland.engine.Adventure> getAdventure(Game game) {
        return Optional.ofNullable(game.getAdventure().getScript())
                .map(AdventureScript::getScript)
                .map(StringReader::new)
                .map(this::parse);
    }

    private GameStateTurnTuple takeTurnInGame(hartman.games.adventureland.engine.Adventure adventure, Game game, String inputCommand) {
        CommandInterpreter interpreter = new StringCommandInterpreter(inputCommand, adventure.getVocabulary());
        StringWriter displayOut = new StringWriter();
        DefaultDisplay display = new DefaultDisplay(new PrintWriter(displayOut));
        GameState gameState = game.load().orElse(new GameState(adventure.getStartRoom(), adventure.getItems()));
        hartman.games.adventureland.engine.Game engineGame = new hartman.games.adventureland.engine.Game(adventure, interpreter, display, gameState);
        gameState = engineGame.takeTurn(interpreter.nextCommand());
        Turn turn = Turn.builder().game(game).command(inputCommand).output(displayOut.toString()).build();
        return new GameStateTurnTuple(gameState, turn);
    }

    private hartman.games.adventureland.engine.Adventure parse(Reader reader) {
        try {
            return parser.parse(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static class StringCommandInterpreter extends DefaultCommandInterpreter {
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
