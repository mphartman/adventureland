package hartman.games.adventureland.api;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
interface GameRepository extends CrudRepository<Game, Long> {

    @Transactional
    List<Game> findByAdventureId(@Param("adventureId") long adventureId);

}
