package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GameResourceProcessor implements RepresentationModelProcessor<EntityModel<Game>> {

    private static final String ADVENTURE_REL = "adventure";
    private static final String TURNS_REL = "turns";
    private static final String TAKETURN_REL = "takeTurn";

    RepositoryEntityLinks entityLinks;

    @Override
    public EntityModel<Game> process(EntityModel<Game> resource) {
        Game game = resource.getContent();
        Long gameId = game.getId();
        Adventure adventure = game.getAdventure();
        Long adventureId = adventure.getId();

        resource.add(linkTo(GameController.class, adventureId, gameId).withSelfRel());
        resource.add(entityLinks.linkToItemResource(adventure, Adventure::getId).withRel(ADVENTURE_REL));
        resource.add(linkTo(methodOn(TurnsController.class, adventureId, gameId).findAllByGameId(gameId)).withRel(TURNS_REL));

        if (game.isReady() || game.isRunning()) {
            resource.add(linkTo(methodOn(TurnsController.class, adventureId, gameId).takeTurn(gameId, TurnsController.TurnDTO.builder().build())).withRel(TAKETURN_REL));
        }

        return resource;
    }
}
