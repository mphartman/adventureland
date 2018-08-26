package hartman.games.adventureland.api;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = {"spring.data.mongodb.port=0"})
public abstract class AbstractIntegrationTest {
}
