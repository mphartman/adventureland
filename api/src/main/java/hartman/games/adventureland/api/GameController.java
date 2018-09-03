package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
    public ResponseEntity<Resource<Game>> findOne(@PathVariable("id") long id) {
        return repository.findById(id)
                .map(this::toResource)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private Resource<Game> toResource(Game game) {
        Resource<Game> resource = new Resource<>(game);
        resource.add(linkTo(GameController.class, game.getAdventure().getId(), game.getId()).withSelfRel());
        resource.add(entityLinks.linkToSingleResource(Adventure.class, game.getAdventure().getId()).withRel("adventure"));
        resource.add(linkTo(methodOn(TurnsController.class, game.getAdventure().getId(), game.getId()).takeTurn(game.getId(), null)).withRel("takeTurn"));
        return resource;
    }

}
