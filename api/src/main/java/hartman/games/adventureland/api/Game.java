package hartman.games.adventureland.api;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
public class Game extends AbstractEntity {

    public enum Status {
        READY, RUNNING, PAUSED, GAME_OVER
    }

    @ManyToOne
    private Adventure adventure;
    private String player;
    private LocalDateTime startTime;
    private Status status = Status.READY;

    public Game() {
    }

    public Adventure getAdventure() {
        return adventure;
    }

    public void setAdventure(Adventure adventure) {
        this.adventure = adventure;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
