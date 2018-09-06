package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RepositoryRestController
@RequestMapping("/adventures/{adventureId}/games/{id}")
public class GameController {

    private GameRepository repository;

    @Autowired
    public GameController(GameRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Resource<Game>> findOne(@PathVariable("id") long id) {
        return repository.findById(id)
                .map(Resource<Game>::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
