package hartman.games.adventureland.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.Identifiable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base class for entities.
 */
@MappedSuperclass
@Getter
@ToString
@EqualsAndHashCode
public class AbstractEntity implements Identifiable<Long> {

	private final @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @JsonIgnore Long id;

	protected AbstractEntity() {
		this.id = null;
	}
}
