package hartman.games.adventureland.api;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface AdventureScriptRepository extends CrudRepository<AdventureScript, Long> {

    Optional<AdventureScript> findByAdventureId(@Param("adventureId") long adventureId);

}
