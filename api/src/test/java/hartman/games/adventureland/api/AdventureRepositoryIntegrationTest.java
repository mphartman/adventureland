package hartman.games.adventureland.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AdventureRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired AdventureRepository repository;

    @Test
    public void createsNewAdventure() {
        Long before = repository.count();

        Adventure adventure = repository.save(createAdventure());

        Iterable<Adventure> adventures = repository.findAll();

        assertThat(adventures).hasSize(before.intValue() + 1);
        assertThat(adventures).contains(adventure);
    }

    private Adventure createAdventure() {
        return new Adventure("Shenanigans", "Archie Hartman", LocalDate.now(), "1.0.0");
    }
}