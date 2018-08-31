package hartman.games.adventureland.api;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface GameRepository extends MongoRepository<Game, String> {

    List<Game> findByAdventureId(String adventureId);

}
