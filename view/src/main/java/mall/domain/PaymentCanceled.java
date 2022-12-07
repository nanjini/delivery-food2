package mall.domain;

import mall.domain.*;
import mall.infra.AbstractEvent;
import java.util.*;
import lombok.*;


@Data
public class PaymentCanceled extends AbstractEvent {

    private Long id;
    private String orderId;

}
