package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class AdventureResourceProcessor implements ResourceProcessor<Resource<Adventure>> {

    private static final String GAMES = "/games";
    private static final String GAMES_REL = "games";
    private static final String START_REL = "start";
    private static final String UPLOAD_REL = "upload";

    private final RepositoryEntityLinks entityLinks;

    @Autowired
    public AdventureResourceProcessor(RepositoryEntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public Resource<Adventure> process(Resource<Adventure> resource) {
        Adventure adventure = resource.getContent();

        resource.add(entityLinks.linkForSingleResource(adventure).slash(GAMES).withRel(GAMES_REL));

        resource.add(entityLinks.linkForSingleResource(adventure).slash(GAMES).withRel(START_REL));

        resource.add(ControllerLinkBuilder.linkTo(AdventureScriptController.class, adventure.getId()).withRel(UPLOAD_REL));

        return resource;
    }
}
