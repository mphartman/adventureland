package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/adventures/{adventureId}/games/{gameId}/turns")
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
            return ResponseEntity.ok(new Resources<>(turnRepository.findByGameId(gameId).stream().map(this::toResource).collect(toList())));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Resource<Turn>> takeTurn(@PathVariable("gameId") long gameId, @RequestBody TurnDTO dto) {
        return gameRepository.findById(gameId)
                .map(game -> gameService.takeTurn(game, dto.getCommand()))
                .map(this::toResource)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Resource<Turn>> findOne(@PathVariable("id") long turnId) {
        return turnRepository.findById(turnId)
                .map(this::toResource)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private Resource<Turn> toResource(Turn turn) {
        Resource<Turn> resource = new Resource<>(turn);
        resource.add(linkTo(methodOn(TurnsController.class, turn.getGame().getAdventure().getId(), turn.getGame().getId(), turn.getId()).findOne(turn.getId())).withSelfRel());
        resource.add(linkTo(methodOn(GameController.class, turn.getGame().getId()).findOne(turn.getGame().getId())).withRel("game"));
        return resource;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TurnDTO {
        private String command;
    }
}
