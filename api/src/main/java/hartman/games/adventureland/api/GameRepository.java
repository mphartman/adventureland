package hartman.games.adventureland.api;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface GameRepository extends PagingAndSortingRepository<Game, Long> {

    List<Game> findByAdventureId(Long adventureId);

}
