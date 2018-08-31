package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping("/adventures/{adventureId}/games/{id}")
public class GameController {

    private GameRepository repository;
    private EntityLinks entityLinks;

    @Autowired
    public GameController(GameRepository repository, EntityLinks entityLinks) {
        this.repository = repository;
        this.entityLinks = entityLinks;
    }

    @GetMapping
    public ResponseEntity<?> getGame(@PathVariable("id") String id) {

        Optional<Game> maybeGame = repository.findById(id);

        if (!maybeGame.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Game game = maybeGame.get();
        Resource<Game> resource = new Resource<>(game);
        resource.add(linkTo(GameController.class, game.getAdventureId(), game.getId()).withSelfRel());
        resource.add(entityLinks.linkToSingleResource(Adventure.class, game.getAdventureId()).withRel("adventure"));
        return ResponseEntity.ok(resource);
    }

}
