package hartman.games.adventureland.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class GameRepositoryIT extends AbstractIntegrationTest {

    @Autowired GameRepository gameRepository;
    @Autowired AdventureRepository adventureRepository;

    @Test
    public void createsNewGame() {
        Adventure adventure = adventureRepository.save(Adventure
                .builder()
                .title("Test Adventure")
                .author("Archie")
                .publishedDate(LocalDate.now())
                .version("0.0.1")
                .build());

        Long before = gameRepository.count();

        Game game = new Game(adventure, "Player One");
        gameRepository.save(game);

        Iterable<Game> games = gameRepository.findAll();

        assertThat(games).hasSize(before.intValue() + 1);
        assertThat(games).contains(game);
    }

    @Test
    public void findsGamesByAdventureId() {
        Adventure adventure1 = adventureRepository.save(Adventure
                .builder()
                .title("Test Adventure 1")
                .author("Archie")
                .publishedDate(LocalDate.now())
                .version("1.0.0")
                .build());
        Adventure adventure2 = adventureRepository.save(Adventure
                .builder()
                .title("Test Adventure 2")
                .author("Jerry")
                .publishedDate(LocalDate.now())
                .version("1.0.0")
                .build());

        Game game11 = gameRepository.save(new Game(adventure1, "Player One"));
        Game game12 = gameRepository.save(new Game(adventure1, "Player Two"));
        Game game21 = gameRepository.save(new Game(adventure2, "Player One"));
        Game game22 = gameRepository.save(new Game(adventure2, "Player Two"));

        assertThat(gameRepository.findByAdventureId(adventure1.getId())).hasSize(2).contains(game11, game12);
        assertThat(gameRepository.findByAdventureId(adventure2.getId())).hasSize(2).contains(game21, game22);
    }
}
