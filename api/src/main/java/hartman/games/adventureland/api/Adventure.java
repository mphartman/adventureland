package hartman.games.adventureland.api;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@Document(collection = "adventures")
public class Adventure {

    @Id
    private String id;

    private String title;
    private String author;
    private LocalDateTime published;
    private String version;

    @Version
    private Long etag;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

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
    public String toString() {
        return new StringJoiner(", ", Adventure.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("title='" + title + "'")
                .add("author='" + author + "'")
                .add("published=" + published)
                .add("version='" + version + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Adventure adventure = (Adventure) o;

        if (!id.equals(adventure.id)) return false;
        return etag.equals(adventure.etag);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + etag.hashCode();
        return result;
    }
}
