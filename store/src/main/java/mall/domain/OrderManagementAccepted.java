package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class OrderManagementAccepted extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String address;
    private String foodType;

    public OrderManagementAccepted(OrderManagement aggregate){
        super(aggregate);
    }
    public OrderManagementAccepted(){
        super();
    }
}
