package hartman.games.adventureland.api;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class AdventureScriptRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired AdventureScriptRepository adventureScriptRepository;
    @Autowired AdventureRepository adventureRepository;

    private Adventure adventure;

    @Before
    public void createAdventure() {
        adventure = adventureRepository.save(new Adventure("Test Adventure", "Archie", LocalDate.now(), "0.0.1"));

    }
    @Before
    public void deleteAll() {
        adventureScriptRepository.deleteAll();
    }

    @Test
    public void createsScript() {
        Long before = adventureScriptRepository.count();

        AdventureScript script = new AdventureScript(adventure, "room forest \"I'm in a forest.\"");
        adventureScriptRepository.save(script);

        Iterable<AdventureScript> scripts = adventureScriptRepository.findAll();

        assertThat(scripts).hasSize(before.intValue() + 1);
        assertThat(scripts).contains(script);
    }
}
