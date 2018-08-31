package hartman.games.adventureland.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.Identifiable;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "games")
public class Game implements Identifiable<String> {

    private @Id String id;
    private @JsonIgnore String adventureId;
    private String player;
    private LocalDateTime startTime;
    private Status status = Status.READY;

    @JsonIgnore
    public boolean isReady() {
        return Status.READY.equals(status);
    }

    @JsonIgnore
    public boolean isSaveable() {
        return !status.equals(Status.GAME_OVER);
    }

    public enum Status {
        /**
         * Game is ready to start accepting player input in the form of turns.
         */
        READY,
        /**
         * Game is currently running and cannot accept a turn.
         */
        RUNNING,
        /**
         * Game is not running and cannot accept player input.
         */
        PAUSED,
        /**
         * Game is over.
         */
        GAME_OVER
    }

}
