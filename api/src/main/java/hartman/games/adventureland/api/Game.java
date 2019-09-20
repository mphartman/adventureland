package hartman.games.adventureland.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import hartman.games.adventureland.engine.GameState;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game implements Identifiable<Long> {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @NonNull @ManyToOne(optional = false) Adventure adventure;
    @NonNull String player;
    LocalDateTime startTime;
    @Builder.Default Status status = Status.READY;
    @JsonIgnore byte[] currentState;
    @JsonIgnore @OneToMany(mappedBy = "game", orphanRemoval = true) @Builder.Default List<Turn> turns = new ArrayList<>();

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

    public boolean isRunning() {
        return Status.RUNNING.equals(status);
    }

    public boolean isReady() {
        return Status.READY.equals(status);
    }

    public boolean isNotGameOver() {
        return Status.GAME_OVER != status;
    }

    public Optional<GameState> currentGameState() {
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
        setStatus(gameState.isRunning() ? Game.Status.RUNNING : Game.Status.GAME_OVER);
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
