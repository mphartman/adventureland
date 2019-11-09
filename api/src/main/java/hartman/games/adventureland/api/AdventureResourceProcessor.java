package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdventureResourceProcessor implements RepresentationModelProcessor<EntityModel<Adventure>> {

    private static final String GAMES = "/games";
    private static final String GAMES_REL = "games";
    private static final String START_REL = "start";
    private static final String UPLOAD_REL = "upload";

    RepositoryEntityLinks entityLinks;

    @Override
    public EntityModel<Adventure> process(EntityModel<Adventure> resource) {
        Adventure adventure = resource.getContent();

        resource.add(entityLinks.linkForItemResource(adventure, Adventure::getId).slash(GAMES).withRel(GAMES_REL));

        if (adventure.getScript() != null) {
            resource.add(entityLinks.linkForItemResource(adventure, Adventure::getId).slash(GAMES).withRel(START_REL));
        }

        resource.add(WebMvcLinkBuilder.linkTo(AdventureScriptController.class, adventure.getId()).withRel(UPLOAD_REL));

        return resource;
    }
}
