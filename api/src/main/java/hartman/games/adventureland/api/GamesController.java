package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RepositoryRestController
@RequestMapping(path = "/adventures/{adventureId}/games")
@CrossOrigin(exposedHeaders = {"Location"})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GamesController {

    GameRepository gameRepository;
    AdventureRepository adventureRepository;
    GameService gameService;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Game>>> findAllByAdventureId(@PathVariable("adventureId") long adventureId) {
        return ResponseEntity.ok(new CollectionModel<>(gameRepository.findByAdventureId(adventureId).stream()
                .map(EntityModel<Game>::new)
                .collect(toList())));
    }

    @PostMapping
    public ResponseEntity<Object> startNewGame(@PathVariable("adventureId") long adventureId, @RequestBody GameDTO gameDto) {
        return adventureRepository.findById(adventureId)
                .filter(adventure -> adventure.getScript() != null)
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
