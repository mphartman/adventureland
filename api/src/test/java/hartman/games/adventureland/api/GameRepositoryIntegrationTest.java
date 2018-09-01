package hartman.games.adventureland.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class GameRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired GameRepository gameRepository;
    @Autowired AdventureRepository adventureRepository;

    @Test
    public void createsNewGame() {
        Adventure adventure = adventureRepository.save(new Adventure("Test Adventure", "Archie", LocalDate.now(), "0.0.1"));

        Long before = gameRepository.count();

        Game game = new Game(adventure, "Player One", LocalDateTime.now(), Game.Status.READY);
        gameRepository.save(game);

        Iterable<Game> games = gameRepository.findAll();

        assertThat(games).hasSize(before.intValue() + 1);
        assertThat(games).contains(game);
    }
}
