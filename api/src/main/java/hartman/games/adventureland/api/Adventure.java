package hartman.games.adventureland.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adventure")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Adventure extends AbstractEntity {

    private @NonNull String title;
    private @NonNull String author;
    private @NonNull LocalDate publishedDate;
    private @NonNull String version;

    @OneToOne
    private AdventureScript script;

    @OrderColumn(nullable = false)
    @Column(unique = true)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Game> games = new ArrayList<>();

}
