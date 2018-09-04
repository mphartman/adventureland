package hartman.games.adventureland.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;

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

    @Test
    public void findsTurnsByGameId() {
        Adventure adventure = adventureRepository.save(new Adventure("Test Adventure", "Tester", LocalDate.now(), "1.0.0"));
        Game game1 = gameRepository.save(new Game(adventure, "Player One"));
        Game game2 = gameRepository.save(new Game(adventure, "Player Two"));
        Game game3 = gameRepository.save(new Game(adventure, "Player Three"));

        Turn turn11 = repository.save(new Turn(game1, "help", "Help is on the way"));
        Turn turn21 = repository.save(new Turn(game2, "help", "Help is on the way"));
        Turn turn22 = repository.save(new Turn(game2, "look", "There are shadows all around."));

        assertThat(repository.findByGameId(game1.getId())).hasSize(1).contains(turn11);
        assertThat(repository.findByGameId(game2.getId())).hasSize(2).contains(turn21, turn22);
        assertThat(repository.findByGameId(game3.getId())).isEmpty();
    }

    @Test
    public void savesLargeAmountOfOutputText() {
        Adventure adventure = adventureRepository.save(new Adventure("Test Adventure", "Tester", LocalDate.now(), "1.0.0"));
        Game game = gameRepository.save(new Game(adventure, "Player One"));
        // create a string made up of 1024 copies of string "*"
        final String largeOutput = String.join("", Collections.nCopies(1024, "*"));
        Turn turn = repository.save(new Turn(game, "help", largeOutput));

        assertThat(repository.findById(turn.getId())).isNotEmpty().hasValueSatisfying(t -> assertThat(t.getOutput()).isEqualTo(largeOutput));
    }
}