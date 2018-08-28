package hartman.games.adventureland.api;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface GameRepository extends PagingAndSortingRepository<Game, Long> {

    List<Game> findByAdventure(@Param("adventure") Adventure adventure);
}
