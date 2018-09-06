package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RepositoryRestController
@RequestMapping(path = "/adventures/{adventureId}/games")
public class GamesController {

    private final GameRepository gameRepository;
    private final AdventureRepository adventureRepository;
    private final GameService gameService;

    @Autowired
    public GamesController(GameRepository gameRepository, AdventureRepository adventureRepository, GameService gameService) {
        this.gameRepository = gameRepository;
        this.adventureRepository = adventureRepository;
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<Resources<Resource<Game>>> findAllByAdventureId(@PathVariable("adventureId") long adventureId) {
        return ResponseEntity.ok(new Resources<>(gameRepository.findByAdventureId(adventureId).stream()
                .map(game -> new Resource<>(game))
                .collect(toList())));
    }

    @PostMapping
    public ResponseEntity<Object> startNewGame(@PathVariable("adventureId") long adventureId, @RequestBody GameDTO gameDto) {
        return adventureRepository.findById(adventureId)
                .map(adventure -> gameService.startNewGame(adventure, gameDto.getPlayer()))
                .map(game -> linkTo(GameController.class, game.getAdventure().getId(), game.getId()))
                .map(link -> ResponseEntity.created(link.toUri()).build())
                .orElse(ResponseEntity.badRequest().build());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GameDTO {
        private String player;
    }
}
