package hartman.games.adventureland.api;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface AdventureScriptRepository extends MongoRepository<AdventureScript, String> {

    Optional<AdventureScript> findByAdventureId(String adventureId);

}
