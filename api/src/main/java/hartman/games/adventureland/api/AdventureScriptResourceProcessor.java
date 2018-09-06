package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
public class AdventureScriptResourceProcessor implements ResourceProcessor<Resource<AdventureScript>> {

    private final EntityLinks entityLinks;

    @Autowired
    public AdventureScriptResourceProcessor(EntityLinks entityLinks) {
        this.entityLinks = entityLinks;
    }

    @Override
    public Resource<AdventureScript> process(Resource<AdventureScript> resource) {
        Adventure adventure = resource.getContent().getAdventure();
        resource.add(entityLinks.linkToSingleResource(adventure).withRel("adventure"));
        resource.add(linkTo(AdventureScriptController.class, adventure.getId()).withSelfRel());
        return resource;
    }
}
