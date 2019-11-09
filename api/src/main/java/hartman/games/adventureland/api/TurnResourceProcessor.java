package hartman.games.adventureland.api;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TurnResourceProcessor implements RepresentationModelProcessor<EntityModel<Turn>> {

    private static final String GAME_REL = "game";

    @Override
    public EntityModel<Turn> process(EntityModel<Turn> resource) {
        Turn turn = resource.getContent();
        Game game = turn.getGame();
        resource.add(linkTo(methodOn(TurnsController.class, game.getAdventure().getId(), game.getId(), turn.getId()).findOne(turn.getId())).withSelfRel());
        resource.add(linkTo(methodOn(GameController.class, game.getId()).findOne(game.getId())).withRel(GAME_REL));
        return resource;
    }
}
