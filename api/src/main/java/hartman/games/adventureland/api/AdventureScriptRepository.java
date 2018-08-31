package hartman.games.adventureland.api;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

interface AdventureScriptRepository extends MongoRepository<AdventureScript, String> {

    Optional<AdventureScript> findByAdventureId(String adventureId);

}
