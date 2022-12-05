package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class OrderManagementStateCompleted extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String address;
    private String foodType;
    private String state;

    public OrderManagementStateCompleted(OrderManagement aggregate){
        super(aggregate);
    }
    public OrderManagementStateCompleted(){
        super();
    }
}
