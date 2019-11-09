package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdventureScriptResourceProcessor implements RepresentationModelProcessor<EntityModel<AdventureScript>> {

    private static final String ADVENTURE_REL = "adventure";

    EntityLinks entityLinks;

    @Override
    public EntityModel<AdventureScript> process(EntityModel<AdventureScript> resource) {
        Adventure adventure = resource.getContent().getAdventure();
        resource.add(entityLinks.linkToItemResource(adventure, Adventure::getId).withRel(ADVENTURE_REL));
        resource.add(linkTo(AdventureScriptController.class, adventure.getId()).withSelfRel());
        return resource;
    }
}
