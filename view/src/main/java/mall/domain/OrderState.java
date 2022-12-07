package mall.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;


@Entity
@Table(name="OrderState_table")
@Data
public class OrderState {

        @Id
        //@GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private String item;
        private String orderState;
        private String payState;


}
