package mall.infra;

import mall.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel="deliveryStates", path="deliveryStates")
public interface DeliveryStateRepository extends PagingAndSortingRepository<DeliveryState, Long> {

    

    
}
