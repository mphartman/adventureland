package hartman.games.adventureland.api;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

interface GameRepository extends PagingAndSortingRepository<Game, String> {

    List<Game> findByAdventure(Adventure adventure);
}
