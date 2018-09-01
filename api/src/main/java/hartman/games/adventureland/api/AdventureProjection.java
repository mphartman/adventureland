package hartman.games.adventureland.api;

import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

@Projection(name = "summary", types = { Adventure.class })
public interface AdventureProjection {

     String getTitle();

     String getAuthor();

     String getVersion();

     LocalDate getPublishedDate();
}
