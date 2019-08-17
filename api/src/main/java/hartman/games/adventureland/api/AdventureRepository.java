package hartman.games.adventureland.api;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@RepositoryRestResource
@CrossOrigin
@XRayEnabled
public interface AdventureRepository extends PagingAndSortingRepository<Adventure, Long> {

}
