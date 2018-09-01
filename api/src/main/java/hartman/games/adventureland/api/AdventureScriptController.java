package hartman.games.adventureland.api;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(path = "/adventures/{id}/upload")
public class AdventureScriptController {

    private AdventureRepository adventureRepository;
    private AdventureScriptRepository repository;
    private EntityLinks entityLinks;

    public AdventureScriptController(AdventureRepository adventureRepository, AdventureScriptRepository repository, EntityLinks entityLinks) {
        this.adventureRepository = adventureRepository;
        this.repository = repository;
        this.entityLinks = entityLinks;
    }

    @GetMapping
    public ResponseEntity<Resource<AdventureScript>> getScriptUpload(@PathVariable("id") Long adventureId) {
        return adventureRepository.findById(adventureId)
                .map(adventure -> repository.findByAdventureId(adventure.getId())
                        .map(script -> toResource(adventure, script))
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Resource<AdventureScript> toResource(Adventure adventure, AdventureScript script) {
        Link adventureRel = entityLinks.linkToSingleResource(adventure).withRel("adventure");
        Link selfRel = linkTo(AdventureScriptController.class, adventure.getId()).withSelfRel();
        return new Resource<>(script, selfRel, adventureRel);
    }

    @PostMapping
    public ResponseEntity<Resource<AdventureScript>> handleScriptUpload(@PathVariable("id") Long adventureId, @RequestParam("file") MultipartFile file) throws IOException {

        Optional<Adventure> maybeAdventure = adventureRepository.findById(adventureId);
        if (!maybeAdventure.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Adventure adventure = maybeAdventure.get();

        String scriptText = fileToString(file);

        AdventureScript adventureScript = repository.findByAdventureId(adventure.getId())
                .orElse(new AdventureScript(adventure, scriptText));

        adventureScript.setScript(scriptText);
        repository.save(adventureScript);

        return ResponseEntity.created(linkTo(AdventureScriptController.class, adventure.getId()).toUri()).build();
    }

    private String fileToString(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            try (InputStream inputStream = file.getInputStream()) {
                return readToString(inputStream);
            }
        }
        return null;
    }

    private String readToString(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}
