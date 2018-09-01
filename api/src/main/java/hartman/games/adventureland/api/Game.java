package hartman.games.adventureland.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "game")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game extends AbstractEntity {

    private @NonNull @ManyToOne Adventure adventure;
    private @NonNull String player;
    private LocalDateTime startTime;
    private Status status = Status.READY;

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
