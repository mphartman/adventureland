package hartman.games.adventureland.api;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

interface GameRepository extends MongoRepository<Game, String> {

    List<Game> findByAdventure(Adventure adventure);

}
