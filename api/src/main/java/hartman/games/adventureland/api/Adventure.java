package hartman.games.adventureland.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.Identifiable;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Validated
public class Adventure implements Identifiable<Long> {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @NotBlank String title;
    @NotBlank String author;
    @NotNull LocalDate publishedDate;
    @NotBlank String version;
    @JsonIgnore @OneToMany(mappedBy = "adventure", orphanRemoval = true) @Builder.Default List<Game> games = new ArrayList<>();
    @JsonIgnore @OneToOne(mappedBy = "adventure", orphanRemoval = true) AdventureScript script;
}
