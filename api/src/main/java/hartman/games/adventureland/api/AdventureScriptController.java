package hartman.games.adventureland.api;

import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(path = "/adventures/{id}/upload")
public class AdventureScriptController {

    private AdventureScriptRepository repository;

    @Autowired
    public AdventureScriptController(AdventureScriptRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<?> getScriptUpload(@PathVariable("id") Adventure adventure) {
        return ResponseEntity.ok(new Resource<>(repository.findByAdventureId(adventure.getId())));
    }

    @PostMapping
    public ResponseEntity<?> handleScriptUpload(@PathVariable("id") Adventure adventure, @RequestParam("file") MultipartFile file) throws IOException {

        String scriptText = fileToString(file);

        AdventureScript adventureScript = repository.findByAdventureId(adventure.getId())
                .orElse(new AdventureScript(null, adventure.getId(), null));

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
