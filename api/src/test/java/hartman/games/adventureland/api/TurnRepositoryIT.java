package hartman.games.adventureland.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnRepositoryIT extends AbstractIntegrationTest {

    @Autowired TurnRepository repository;
    @Autowired GameRepository gameRepository;
    @Autowired AdventureRepository adventureRepository;

    @Test
    public void createsNewTurn() {
        Long before = repository.count();

        Adventure adventure = adventureRepository.save(newAdventure());
        Game game = gameRepository.save(Game.builder().adventure(adventure).player("Player One").build());
        Turn turn = repository.save(Turn.builder().game(game).command("help").output("Help is on the way").build());

        Iterable<Turn> turns = repository.findAll();

        assertThat(turns).hasSize(before.intValue() + 1);
        assertThat(turns).contains(turn);
    }

    private Adventure newAdventure() {
        return Adventure
                .builder()
                .title("Test Adventure")
                .author("Tester")
                .publishedDate(LocalDate.now())
                .version("1.0.0")
                .build();
    }

    @Test
    public void findsTurnsByGameId() {
        Adventure adventure = adventureRepository.save(newAdventure());
        Game game1 = gameRepository.save(Game.builder().adventure(adventure).player("Player One").build());
        Game game2 = gameRepository.save(Game.builder().adventure(adventure).player("Player Two").build());
        Game game3 = gameRepository.save(Game.builder().adventure(adventure).player("Player Three").build());

        Turn turn11 = repository.save(Turn.builder().game(game1).command("help").output("Help is on the way").build());
        Turn turn21 = repository.save(Turn.builder().game(game2).command("help").output("Help is on the way").build());
        Turn turn22 = repository.save(Turn.builder().game(game2).command("look").output("There are shadows all around.").build());

        assertThat(repository.findByGameId(game1.getId())).hasSize(1).contains(turn11);
        assertThat(repository.findByGameId(game2.getId())).hasSize(2).contains(turn21, turn22);
        assertThat(repository.findByGameId(game3.getId())).isEmpty();
    }

    @Test
    public void savesLargeAmountOfOutputText() {
        Adventure adventure = adventureRepository.save(newAdventure());
        Game game = gameRepository.save(Game.builder().adventure(adventure).player("Player One").build());
        // create a string made up of 1024 copies of string "*"
        final String largeOutput = String.join("", Collections.nCopies(1024, "*"));
        Turn turn = repository.save(Turn.builder().game(game).command("help").output(largeOutput).build());

        assertThat(repository.findById(turn.getId())).isNotEmpty().hasValueSatisfying(t -> assertThat(t.getOutput()).isEqualTo(largeOutput));
    }
}