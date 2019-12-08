package hartman.games.adventureland.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
interface TurnRepository extends PagingAndSortingRepository<Turn, Long> {

    @Transactional
    List<Turn> findByGameId(@Param("gameId") long gameId);

    @Transactional
    Page<Turn> findAllByGameId(@Param("gameId") long gameId, Pageable pageable);
}
