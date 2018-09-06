package hartman.games.adventureland.api;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class TurnResourceProcessor implements ResourceProcessor<Resource<Turn>> {
    @Override
    public Resource<Turn> process(Resource<Turn> resource) {
        Turn turn = resource.getContent();
        Game game = turn.getGame();
        resource.add(linkTo(methodOn(TurnsController.class, game.getAdventure().getId(), game.getId(), turn.getId()).findOne(turn.getId())).withSelfRel());
        resource.add(linkTo(methodOn(GameController.class, game.getId()).findOne(game.getId())).withRel("game"));
        return resource;
    }
}
