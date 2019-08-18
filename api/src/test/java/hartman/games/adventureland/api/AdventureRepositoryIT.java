package hartman.games.adventureland.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AdventureRepositoryIT extends AbstractIntegrationTest {

    @Autowired AdventureRepository repository;

    @Test
    public void createsNewAdventure() {
        Long before = repository.count();

        Adventure adventure = repository.save(createAdventure());

        Iterable<Adventure> adventures = repository.findAll();

        assertThat(adventures).hasSize(before.intValue() + 1);
        assertThat(adventures).contains(adventure);
    }

    @Test
    public void savesGames() {
        Adventure adventure = createAdventure();
        Game game = Game.builder().adventure(adventure).player("Michael").build();
        adventure.getGames().add(game);
        adventure = repository.save(adventure);

        assertThat(repository.findById(adventure.getId())).hasValueSatisfying(a -> assertThat(a.getGames()).hasSize(1).contains(game));
    }

    private Adventure createAdventure() {
        return Adventure
                .builder()
                .title("Shenanigans")
                .author("Archie Hartman")
                .publishedDate(LocalDate.now())
                .version("1.0.0")
                .build();
    }
}