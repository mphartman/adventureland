package hartman.games.adventureland.api;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface AdventureRepository extends PagingAndSortingRepository<Adventure, Long> {

}
