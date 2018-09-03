package hartman.games.adventureland.api;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface AdventureScriptRepository extends CrudRepository<AdventureScript, Long> {

    Optional<AdventureScript> findByAdventureId(@Param("adventureId") long adventureId);

}
