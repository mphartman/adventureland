package hartman.games.adventureland.api;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

interface TurnRepository extends CrudRepository<Turn, Long> {

    @Transactional
    List<Turn> findByGameId(@Param("gameId") long gameId);

}
