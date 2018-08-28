package hartman.games.adventureland.api;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.Identifiable;

import java.time.LocalDateTime;

@Document(collection = "adventures")
public class Adventure implements Identifiable<String> {

    @Id
    private String id;

    private String title;
    private String author;
    private LocalDateTime published;
    private String version;

    @Override
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public void setPublished(LocalDateTime published) {
        this.published = published;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Adventure adventure = (Adventure) o;

        return id.equals(adventure.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
