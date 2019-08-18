package hartman.games.adventureland.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.hateoas.Identifiable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Information about an adventure story.
 */
@Entity
@Table(name = "adventure")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Adventure implements Identifiable<Long> {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @NonNull String title;
    @NonNull String author;
    @NonNull LocalDate publishedDate;
    @NonNull String version;
    @JsonIgnore @OneToMany(mappedBy = "adventure", orphanRemoval = true) @Builder.Default List<Game> games = new ArrayList<>();
    @JsonIgnore @OneToOne(mappedBy = "adventure", orphanRemoval = true) AdventureScript script;
}
