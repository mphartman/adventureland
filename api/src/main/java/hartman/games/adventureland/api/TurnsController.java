package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RepositoryRestController
@RequestMapping("/adventures/{adventureId}/games/{gameId}/turns")
@CrossOrigin
public class TurnsController {

    private final GameService gameService;
    private final GameRepository gameRepository;
    private final TurnRepository turnRepository;

    @Autowired
    public TurnsController(GameService gameService, GameRepository gameRepository, TurnRepository turnRepository) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.turnRepository = turnRepository;
    }

    @GetMapping
    public ResponseEntity<Resources<Resource<Turn>>> findAllByGameId(@PathVariable("gameId") long gameId) {
        if (gameRepository.existsById(gameId)) {
            return ResponseEntity.ok(new Resources<>(turnRepository.findByGameId(gameId).stream()
                    .map(Resource<Turn>::new)
                    .collect(toList())));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<Resource<Turn>> takeTurn(@PathVariable("gameId") long gameId, @RequestBody TurnDTO dto) {
        return gameRepository.findById(gameId)
                .filter(game -> game.getStatus() != Game.Status.GAME_OVER)
                .map(game -> gameService.takeTurn(game, dto.getCommand()))
                .map(turn -> {
                    ControllerLinkBuilder link = linkTo(methodOn(TurnsController.class, turn.getGame().getAdventure().getId(), turn.getGame().getId()).findOne(turn.getId()));
                    return ResponseEntity.created(link.toUri()).body(new Resource<>(turn));
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Resource<Turn>> findOne(@PathVariable("id") long turnId) {
        return turnRepository.findById(turnId)
                .map(Resource<Turn>::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TurnDTO {
        private String command;
    }
}
