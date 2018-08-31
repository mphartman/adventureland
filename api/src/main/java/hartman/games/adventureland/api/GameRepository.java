package hartman.games.adventureland.api;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
interface GameRepository extends MongoRepository<Game, String> {

    @RestResource(path = "byAdventure", rel = "adventure")
    List<Game> findByAdventureId(String adventureId);

}
