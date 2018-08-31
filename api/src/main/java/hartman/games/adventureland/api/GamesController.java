package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(path = "/adventures/{adventureId}/games")
public class GamesController {

    private GameRepository repository;

    @Autowired
    public GamesController(GameRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<?> getGames(@PathVariable("adventureId") Adventure adventure) {
        List<Game> games = repository.findByAdventureId(adventure.getId());

        List<Resource<Game>> resources = games.stream()
                .map(game -> new Resource<>(game, linkTo(GameController.class, adventure.getId(), game.getId()).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new Resources<>(resources));
    }

    @PostMapping
    public ResponseEntity<?> startNewGame(@PathVariable("adventureId") Adventure adventure, @RequestBody Game game) {

        game.setId(null);
        game.setStatus(Game.Status.READY);
        game.setAdventureId(adventure.getId());
        if (null == game.getStartTime()) {
            game.setStartTime(LocalDateTime.now());
        }
        game = repository.save(game);

        ControllerLinkBuilder selfLink = linkTo(GameController.class, adventure.getId(), game.getId());
        Resource<Game> resource = new Resource<>(game, selfLink.withSelfRel());
        URI location = selfLink.toUri();
        return ResponseEntity.created(location).body(resource);
    }
}
