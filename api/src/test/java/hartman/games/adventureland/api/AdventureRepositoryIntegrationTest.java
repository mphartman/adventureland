package hartman.games.adventureland.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdventureRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    AdventureRepository repository;

    @Test
    public void createsNewAdventure() {
        Long before = repository.count();

        Adventure adventure = repository.save(createAdventure());

        Iterable<Adventure> adventures = repository.findAll();

        assertThat(adventures).hasSize(before.intValue() + 1);
        assertThat(adventures).contains(adventure);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByTitleRequiresNonNullTitle() {
        repository.findByTitle(null);
    }

    @Test
    public void findByTitle() {
        assertThat(repository.count()).isZero();

        assertThat(repository.findByTitle("Does not Exist")).isEmpty();

        Adventure expected = repository.save(createAdventure());

        Optional<Adventure> result = repository.findByTitle(expected.getTitle());

        assertThat(result).hasValue(expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findByAuthorRequiresNonNullAuthor() {
        repository.findByAuthor(null, null);
    }

    @Test
    public void findByAuthor() {
        Page<Adventure> page = repository.findByAuthor("Archie Hartman", Pageable.unpaged());
        assertThat(page.getTotalElements()).isEqualTo(0);

        Adventure adventure = repository.save(createAdventure());

        page = repository.findByAuthor("Archie Hartman", Pageable.unpaged());
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).contains(adventure);
    }

    private Adventure createAdventure() {
        Adventure adventure = new Adventure();
        adventure.setTitle("Shenanigans");
        adventure.setAuthor("Archie Hartman");
        adventure.setVersion("0.0.1");
        adventure.setPublished(LocalDateTime.of(2018, 8, 27, 13, 28, 0));
        return adventure;
    }
}