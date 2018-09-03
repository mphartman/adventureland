package hartman.games.adventureland.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
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
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Turn extends AbstractEntity {

    private @JsonIgnore @NonNull @ManyToOne(optional = false) Game game;
    private @OrderColumn LocalDateTime timestamp = LocalDateTime.now();
    private @NonNull String command;
    private @NonNull String output;
}
