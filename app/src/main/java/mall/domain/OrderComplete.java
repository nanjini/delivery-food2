package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
@ToString
public class OrderComplete extends AbstractEvent {

    private Long id;

    public OrderComplete(Order aggregate){
        super(aggregate);
    }
    public OrderComplete(){
        super();
    }
}
