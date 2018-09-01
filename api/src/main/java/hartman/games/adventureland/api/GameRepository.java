package hartman.games.adventureland.api;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface GameRepository extends CrudRepository<Game, Long> {

    List<Game> findByAdventureId(Long adventureId);

}
