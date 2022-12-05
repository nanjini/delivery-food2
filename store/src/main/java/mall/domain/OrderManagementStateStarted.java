package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class OrderManagementStateStarted extends AbstractEvent {

    private Long id;
    private String state;

    public OrderManagementStateStarted(OrderManagement aggregate){
        super(aggregate);
    }
    public OrderManagementStateStarted(){
        super();
    }
}
