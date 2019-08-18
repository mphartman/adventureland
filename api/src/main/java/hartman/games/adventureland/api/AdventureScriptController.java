package hartman.games.adventureland.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RepositoryRestController
@RequestMapping(path = "/adventures/{id}/upload")
@CrossOrigin
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdventureScriptController {

    AdventureRepository adventureRepository;
    AdventureScriptRepository adventureScriptRepository;

    @GetMapping
    public ResponseEntity<Resource<AdventureScript>> findOneByAdventureId(@PathVariable("id") long adventureId) {
        return adventureRepository.findById(adventureId)
                .flatMap(adventure -> adventureScriptRepository.findByAdventureId(adventure.getId()))
                .map(Resource<AdventureScript>::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> handleScriptUpload(@PathVariable("id") long adventureId, @RequestParam("file") MultipartFile file) throws IOException {

        String scriptText = fileToString(file);

        return adventureRepository.findById(adventureId)
                .map(adventure -> adventureScriptRepository.findByAdventureId(adventure.getId()).orElse(AdventureScript.builder().adventure(adventure).script(scriptText).build()))
                .map(script -> update(script, scriptText))
                .map(script -> linkTo(AdventureScriptController.class, script.getAdventure().getId()))
                .map(builder -> ResponseEntity.created(builder.toUri()).build())
                .orElse(ResponseEntity.badRequest().build());
    }

    private AdventureScript update(AdventureScript script, String scriptText) {
        script.setScript(scriptText);
        return adventureScriptRepository.save(script);
    }

    private String fileToString(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            try (InputStream inputStream = file.getInputStream()) {
                return readToString(inputStream);
            }
        }
        return "";
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
