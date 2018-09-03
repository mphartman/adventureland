package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(path = "/adventures/{adventureId}/games")
public class GamesController {

    private GameRepository repository;
    private AdventureRepository adventureRepository;

    @Autowired
    public GamesController(GameRepository repository, AdventureRepository adventureRepository) {
        this.repository = repository;
        this.adventureRepository = adventureRepository;
    }

    @GetMapping
    public ResponseEntity<Resources<Resource<Game>>> getGames(@PathVariable("adventureId") Long adventureId) {
        List<Game> games = repository.findByAdventureId(adventureId);

        List<Resource<Game>> resources = games.stream()
                .map(game -> new Resource<>(game, linkTo(GameController.class, adventureId, game.getId()).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new Resources<>(resources));
    }

    @PostMapping
    public ResponseEntity<Resource<Game>> startNewGame(@PathVariable("adventureId") Long adventureId, @RequestBody GameDTO gameDto) {

        Optional<Adventure> maybeAdventure = adventureRepository.findById(adventureId);
        if (!maybeAdventure.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Adventure adventure = maybeAdventure.get();

        Game game = new Game(adventure, gameDto.getPlayer());
        game.setStatus(Game.Status.READY);
        game.setStartTime(LocalDateTime.now());
        game = repository.save(game);

        ControllerLinkBuilder selfLink = linkTo(GameController.class, adventure.getId(), game.getId());
        Resource<Game> resource = new Resource<>(game, selfLink.withSelfRel());
        URI location = selfLink.toUri();

        return ResponseEntity.created(location).body(resource);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GameDTO {
        private String player;
    }
}
