package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class GameResourceProcessor implements ResourceProcessor<Resource<Game>> {

    private EntityLinks entityLinks;

    @Autowired
    public GameResourceProcessor(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public Resource<Game> process(Resource<Game> resource) {
        Game game = resource.getContent();

        if (game.isReady()) {
            resource.add(entityLinks.linkForSingleResource(game).slash("/turn").withRel("takeTurn"));
        }

        if (game.isSaveable()) {
            resource.add(entityLinks.linkForSingleResource(game).slash("/saves").withRel("save"));
        }

        resource.add(entityLinks.linkToSingleResource(Adventure.class, game.getAdventureId()).withRel("adventure"));

        return resource;
    }
}
