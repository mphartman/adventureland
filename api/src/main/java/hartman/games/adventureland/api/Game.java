package hartman.games.adventureland.api;

import org.springframework.data.annotation.Id;
import org.springframework.hateoas.Identifiable;

import java.time.LocalDateTime;

public class Game implements Identifiable<String> {

    public enum Status {
        READY, RUNNING, PAUSED, GAME_OVER
    }

    @Id
    private String id;

    private Adventure adventure;
    private String player;
    private LocalDateTime startTime;
    private Status status = Status.READY;

    @Override
    public String getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return id.equals(game.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
