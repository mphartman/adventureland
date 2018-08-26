package hartman.games.adventureland.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "adventures", path = "adventures")
public interface AdventureRepository extends MongoRepository<Adventure, String> {

    @RestResource(path = "byTitle", rel = "title")
    Optional<Adventure> findByTitle(@Param("title") String title);

    @RestResource(path = "byAuthor", rel = "author")
    Page<Adventure> findByAuthor(@Param("author") String author, Pageable pageable);

}
