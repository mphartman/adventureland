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
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * A turn of a game which represents a player's input and the game's response as output.
 */
@Entity
@Table(name = "turn")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Turn  implements Identifiable<Long> {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @JsonIgnore Long id;
    @NonNull @ManyToOne(optional = false) @JsonIgnore Game game;
    @OrderColumn @Builder.Default LocalDateTime timestamp = LocalDateTime.now();
    @NonNull String command;
    @NonNull String output;
}
