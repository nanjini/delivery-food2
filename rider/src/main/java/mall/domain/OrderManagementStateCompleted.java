package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import lombok.*;
import java.util.*;
@Data
@ToString
public class OrderManagementStateCompleted extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String address;
    private String foodType;
    private String state;
}


