package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import lombok.*;
import java.util.*;
@Data
@ToString
public class OrderManagementStateStarted extends AbstractEvent {

    private Long id;
    private String state;
}


