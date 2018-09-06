package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class GameResourceProcessor implements ResourceProcessor<Resource<Game>> {

    private final RepositoryEntityLinks entityLinks;

    @Autowired
    public GameResourceProcessor(RepositoryEntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public Resource<Game> process(Resource<Game> resource) {
        Game game = resource.getContent();
        Adventure adventure = game.getAdventure();

        resource.add(linkTo(GameController.class, adventure.getId(), game.getId()).withSelfRel());
        resource.add(entityLinks.linkToSingleResource(adventure).withRel("adventure"));

        if (game.getStatus().equals(Game.Status.READY) || game.getStatus().equals(Game.Status.RUNNING)) {
            resource.add(linkTo(methodOn(TurnsController.class, adventure.getId(), game.getId()).takeTurn(game.getId(), null)).withRel("takeTurn"));
        }

        return resource;
    }
}
