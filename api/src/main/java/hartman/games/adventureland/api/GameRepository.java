package hartman.games.adventureland.api;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

interface GameRepository extends CrudRepository<Game, Long> {

    @Transactional
    List<Game> findByAdventureId(@Param("adventureId") long adventureId);

}
