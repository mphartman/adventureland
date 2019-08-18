package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdventureScriptResourceProcessor implements ResourceProcessor<Resource<AdventureScript>> {

    private static final String ADVENTURE_REL = "adventure";

    EntityLinks entityLinks;

    @Override
    public Resource<AdventureScript> process(Resource<AdventureScript> resource) {
        Adventure adventure = resource.getContent().getAdventure();
        resource.add(entityLinks.linkToSingleResource(adventure).withRel(ADVENTURE_REL));
        resource.add(linkTo(AdventureScriptController.class, adventure.getId()).withSelfRel());
        return resource;
    }
}
