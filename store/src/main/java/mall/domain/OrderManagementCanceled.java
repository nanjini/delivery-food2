package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class OrderManagementCanceled extends AbstractEvent {

    private Long id;

    public OrderManagementCanceled(OrderManagement aggregate){
        super(aggregate);
    }
    public OrderManagementCanceled(){
        super();
    }
}
