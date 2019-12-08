package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RepositoryRestController
@RequestMapping("/adventures/{adventureId}/games/{gameId}/turns")
@CrossOrigin
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TurnsController {

    GameService gameService;
    GameRepository gameRepository;
    TurnRepository turnRepository;
    PagedResourcesAssembler<Turn> assembler;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Turn>>> findAllByGameId(@PathVariable("gameId") long gameId, @PageableDefault(size = 20) Pageable pageable) {
        if (gameRepository.existsById(gameId)) {
            Page<Turn> page = turnRepository.findAllByGameId(gameId, pageable);
            PagedModel<EntityModel<Turn>> pagedModel = assembler.toModel(page);
            return ResponseEntity.ok(pagedModel);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<EntityModel<Turn>> takeTurn(@PathVariable("gameId") long gameId, @RequestBody TurnDTO dto) {
        return gameRepository.findById(gameId)
                .filter(Game::isNotGameOver)
                .map(game -> gameService.takeTurn(game, dto.getCommand()))
                .map(turn -> {
                    WebMvcLinkBuilder link = linkTo(methodOn(TurnsController.class, turn.getGame().getAdventure().getId(), turn.getGame().getId()).findOne(turn.getId()));
                    return ResponseEntity.created(link.toUri()).body(new EntityModel<>(turn));
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<EntityModel<Turn>> findOne(@PathVariable("id") long turnId) {
        return turnRepository.findById(turnId)
                .map(EntityModel<Turn>::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TurnDTO {
        String command;
    }
}
