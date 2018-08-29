package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/adventures/{id}")
@ExposesResourceFor(Adventure.class)
public class AdventureController {

    private final AdventureRepository repository;
    private final GameRepository gameRepository;
    private final RepositoryEntityLinks entityLinks;

    @Autowired
    public AdventureController(AdventureRepository repository, GameRepository gameRepository, RepositoryEntityLinks entityLinks) {
        this.repository = repository;
        this.gameRepository = gameRepository;
        this.entityLinks = entityLinks;
    }

    @RequestMapping(path = "/games", method = GET)
    public @ResponseBody ResponseEntity<?> listGames(@PathVariable("id") Adventure adventure) {

        System.out.println("GET games");

        List<Game> games = gameRepository.findByAdventure(adventure);

        Resources<Resource<Game>> resources = Resources.wrap(games);
        resources.add(entityLinks.linkToCollectionResource(Game.class));

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(path = "/games", method = POST)
    public @ResponseBody ResponseEntity<?> startNewGame(@PathVariable("id") Adventure adventure, @RequestBody Game game, UriComponentsBuilder b) {
        System.out.println("Start new game " + game);

        game.setAdventure(adventure);
        game.setStatus(Game.Status.READY);
        if (null == game.getStartTime()) {
            game.setStartTime(LocalDateTime.now());
        }
        game = gameRepository.save(game);

        System.out.println("Game ID: " + game.getId());

        //UriComponents uriComponents = b.path("/adventures/{adventureId}/games/{id}").buildAndExpand(adventure.getId(), game.getId());

        Resource<Game> resource = new Resource<>(game);
        resource.add(entityLinks.linkForSingleResource(adventure).slash("/games").slash(game).withSelfRel());

        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }
}
