package hartman.games.adventureland.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hartman.games.adventureland.engine.GameState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An instance of an adventure being played.
 */
@Entity
@Table(name = "game")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game extends AbstractEntity {

    private @NonNull @ManyToOne(optional = false) Adventure adventure;
    private @NonNull String player;
    private LocalDateTime startTime;
    private Status status = Status.READY;
    private @JsonIgnore byte[] currentState;
    private @JsonIgnore @OneToMany(mappedBy = "game", orphanRemoval = true) List<Turn> turns = new ArrayList<>();

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
         * Game is over.
         */
        GAME_OVER
    }

    public Optional<GameState> load() {
        if (getCurrentState() == null) {
            return Optional.empty();
        }
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(getCurrentState());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return Optional.of((GameState) ois.readObject());
        }
        catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public Game update(GameState gameState) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(gameState);
            setCurrentState(baos.toByteArray());
            return this;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
