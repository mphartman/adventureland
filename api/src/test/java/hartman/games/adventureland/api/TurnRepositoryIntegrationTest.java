package hartman.games.adventureland.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired TurnRepository repository;
    @Autowired GameRepository gameRepository;
    @Autowired AdventureRepository adventureRepository;

    @Test
    public void createsNewTurn() {
        Long before = repository.count();

        Adventure adventure = adventureRepository.save(new Adventure("Test Adventure", "Tester", LocalDate.now(), "1.0.0"));
        Game game = gameRepository.save(new Game(adventure, "Player One"));
        Turn turn = repository.save(new Turn(game, "help", "Help is on the way"));

        Iterable<Turn> turns = repository.findAll();

        assertThat(turns).hasSize(before.intValue() + 1);
        assertThat(turns).contains(turn);
    }

}