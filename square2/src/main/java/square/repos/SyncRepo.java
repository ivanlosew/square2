package square.repos;

import org.springframework.data.repository.CrudRepository;
import square.domain.Sync;

public interface SyncRepo extends CrudRepository<Sync, Integer> {

}
