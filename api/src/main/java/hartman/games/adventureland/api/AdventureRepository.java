package hartman.games.adventureland.api;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface AdventureRepository extends MongoRepository<Adventure, String> {

}
