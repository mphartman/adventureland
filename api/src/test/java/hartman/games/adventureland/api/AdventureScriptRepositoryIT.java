package hartman.games.adventureland.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AdventureScriptRepositoryIT extends AbstractIntegrationTest {

    @Autowired AdventureScriptRepository adventureScriptRepository;
    @Autowired AdventureRepository adventureRepository;

    @Test
    public void createsScript() {
        Adventure adventure = saveNewAdventure();

        long before = adventureScriptRepository.count();

        AdventureScript script = AdventureScript
                .builder()
                .adventure(adventure)
                .script("room forest \"I'm in a forest.\"")
                .build();
        adventureScriptRepository.save(script);

        Iterable<AdventureScript> scripts = adventureScriptRepository.findAll();
        assertThat(scripts).hasSize((int) (before + 1));
        assertThat(scripts).contains(script);
    }

    private Adventure saveNewAdventure() {
        return adventureRepository.save(Adventure
                .builder()
                .title("Test Adventure")
                .author("Archie")
                .publishedDate(LocalDate.now())
                .version("0.0.1")
                .build());
    }

    @Test
    public void deletesScript() {
        Adventure adventure = saveNewAdventure();

        AdventureScript script = AdventureScript
                .builder()
                .adventure(adventure)
                .script("room forest \"I'm in a forest.\"")
                .build();
        script = adventureScriptRepository.save(script);

        long count = adventureScriptRepository.count();

        adventureScriptRepository.delete(script);

        Iterable<AdventureScript> scripts = adventureScriptRepository.findAll();
        assertThat(scripts).hasSize((int) (count - 1));
        assertThat(scripts).doesNotContain(script);
        assertThat(adventureScriptRepository.findById(script.getId())).isEmpty();
    }
}
